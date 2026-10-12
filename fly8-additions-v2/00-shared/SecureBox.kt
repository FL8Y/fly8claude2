package com.fly8.clone.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Encrypted small-file storage, the same design EysClone uses for its Discord webhook
 * (REA/JADX: class yb — AndroidKeyStore AES-256-GCM key "eys-discord-alerts-v1",
 * AtomicFile "discord-alerts.bin" in noBackupFilesDir, package name as associated data).
 *
 * The key never leaves the Keystore, the file is excluded from backups, and a copied
 * file cannot be decrypted on another device.
 */
class SecureBox(context: Context, private val alias: String, fileName: String) {
    private val file = AtomicFile(File(context.noBackupFilesDir, fileName))
    private val aad = (context.packageName + ":" + alias).toByteArray()

    private fun key(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getKey(alias, null) as? SecretKey)?.let { return it }
        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        gen.init(
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(256)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return gen.generateKey()
    }

    /** Returns null when nothing is stored or the data cannot be decrypted. */
    fun read(): String? = runCatching {
        val bytes = file.readFully()
        require(bytes.size in 13..8192) { "bad size" }
        val iv = bytes.copyOfRange(0, 12)
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, iv))
        c.updateAAD(aad)
        String(c.doFinal(bytes, 12, bytes.size - 12))
    }.getOrNull()

    fun write(text: String): Boolean = runCatching {
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, key())
        c.updateAAD(aad)
        val out = c.iv + c.doFinal(text.toByteArray())
        val s = file.startWrite()
        try { s.write(out); file.finishWrite(s) } catch (t: Throwable) { file.failWrite(s); throw t }
    }.isSuccess

    fun delete() = file.delete()
}
