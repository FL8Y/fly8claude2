package com.fly8.clone

import com.fly8.clone.data.LogStore
import com.fly8.clone.data.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {
    @Test fun packageNames() {
        assertTrue(Validators.isPackageName("com.roblox.client"))
        assertTrue(Validators.isPackageName("com.digibites.accubattery"))
        assertFalse(Validators.isPackageName("roblox"))
        assertFalse(Validators.isPackageName("com.evil; rm -rf"))
        assertFalse(Validators.isPackageName("com .space.pkg"))
        assertFalse(Validators.isPackageName("1com.bad.start"))
    }

    @Test fun pairingCode() {
        assertTrue(Validators.isPairingCode("123456"))
        assertFalse(Validators.isPairingCode("12345"))
        assertFalse(Validators.isPairingCode("12345a"))
    }

    @Test fun ports() {
        assertTrue(Validators.isPort(37555))
        assertFalse(Validators.isPort(0))
        assertFalse(Validators.isPort(70000))
    }

    @Test fun logRedactsSecrets() {
        assertEquals("pairing code=[redacted]", LogStore.redact("pairing code: 123456"))
        assertTrue(LogStore.redact("code 654321 used").contains("[redacted]"))
        assertFalse(LogStore.redact("token=abcd1234").contains("abcd1234"))
    }
}
