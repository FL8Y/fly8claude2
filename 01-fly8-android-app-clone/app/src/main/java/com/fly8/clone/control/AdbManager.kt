package com.fly8.clone.control

import android.content.Context
import android.os.Build
import android.sun.security.x509.AlgorithmId
import android.sun.security.x509.CertificateAlgorithmId
import android.sun.security.x509.CertificateExtensions
import android.sun.security.x509.CertificateIssuerName
import android.sun.security.x509.CertificateSerialNumber
import android.sun.security.x509.CertificateSubjectName
import android.sun.security.x509.CertificateValidity
import android.sun.security.x509.CertificateVersion
import android.sun.security.x509.CertificateX509Key
import android.sun.security.x509.KeyIdentifier
import android.sun.security.x509.PrivateKeyUsageExtension
import android.sun.security.x509.SubjectKeyIdentifierExtension
import android.sun.security.x509.X500Name
import android.sun.security.x509.X509CertImpl
import android.sun.security.x509.X509CertInfo
import io.github.muntashirakon.adb.AbsAdbConnectionManager
import java.io.File
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Wireless debugging (ADB over TLS) client built on LibADB. Pairing uses SPAKE2 inside LibADB;
 * the six-digit code only lives for the duration of [pair] and is never stored.
 */
class AdbManager private constructor(context: Context) : AbsAdbConnectionManager() {
    private val key: PrivateKey
    private val cert: Certificate

    init {
        setApi(Build.VERSION.SDK_INT)
        setTimeout(10, TimeUnit.SECONDS)
        val material = loadOrCreate(context)
        key = material.first
        cert = material.second
    }

    override fun getPrivateKey() = key
    override fun getCertificate() = cert
    override fun getDeviceName() = "FLY8"

    /** Runs one non-interactive shell command and returns (exitCode, output). */
    fun shell(command: String): Pair<Int, String> {
        val marker = EXIT_MARKER
        openStream("shell:" + withExitMarker(command)).use { stream ->
            val input = stream.openInputStream()
            val out = StringBuilder()
            val buf = ByteArray(8192)
            while (true) {
                val n = try { input.read(buf) } catch (e: java.io.IOException) { -1 }
                if (n < 0) break
                out.append(String(buf, 0, n, Charsets.UTF_8))
            }
            val text = out.toString()
            val idx = text.lastIndexOf(marker)
            if (idx < 0) return -1 to text.trim()
            val code = text.substring(idx + marker.length).trim().toIntOrNull() ?: -1
            return code to text.substring(0, idx).trim()
        }
    }

    companion object {
        @Volatile private var instance: AdbManager? = null
        fun get(context: Context): AdbManager = instance ?: synchronized(this) {
            instance ?: AdbManager(context.applicationContext).also { instance = it }
        }

        private fun loadOrCreate(context: Context): Pair<PrivateKey, Certificate> {
            val keyFile = File(context.filesDir, "adb_key.der")
            val certFile = File(context.filesDir, "adb_cert.der")
            if (keyFile.exists() && certFile.exists()) {
                runCatching {
                    val k = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(keyFile.readBytes()))
                    val c = certFile.inputStream().use { CertificateFactory.getInstance("X.509").generateCertificate(it) }
                    return k to c
                }
            }
            val gen = KeyPairGenerator.getInstance("RSA").apply { initialize(2048, SecureRandom()) }
            val pair = gen.generateKeyPair()
            val c = certificate(pair.public, pair.private)
            keyFile.writeBytes(pair.private.encoded)
            certFile.writeBytes(c.encoded)
            return pair.private to c
        }

        private fun certificate(publicKey: PublicKey, privateKey: PrivateKey): Certificate {
            val alg = "SHA512withRSA"
            val notBefore = Date(System.currentTimeMillis() - 60_000)
            val notAfter = Date(System.currentTimeMillis() + 3650L * 24 * 60 * 60 * 1000)
            val ext = CertificateExtensions()
            ext.set("SubjectKeyIdentifier", SubjectKeyIdentifierExtension(KeyIdentifier(publicKey).identifier))
            ext.set("PrivateKeyUsage", PrivateKeyUsageExtension(notBefore, notAfter))
            val name = X500Name("CN=FLY8")
            val info = X509CertInfo()
            info.set("version", CertificateVersion(2))
            info.set("serialNumber", CertificateSerialNumber(SecureRandom().nextInt() and Int.MAX_VALUE))
            info.set("algorithmID", CertificateAlgorithmId(AlgorithmId.get(alg)))
            info.set("subject", CertificateSubjectName(name))
            info.set("key", CertificateX509Key(publicKey))
            info.set("validity", CertificateValidity(notBefore, notAfter))
            info.set("issuer", CertificateIssuerName(name))
            info.set("extensions", ext)
            return X509CertImpl(info).also { it.sign(privateKey, alg) }
        }
    }
}

internal const val EXIT_MARKER = "__FLY8_RC__"

/**
 * Appends an exit-code marker on its own line. A `;` separator breaks any command that ends in
 * `&` (`cmd &; echo` is a shell syntax error), which is exactly how the control server launches.
 */
internal fun withExitMarker(command: String) = "${command.trimEnd()}\necho $EXIT_MARKER\$?"
