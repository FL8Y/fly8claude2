package com.fly8.clone

import com.fly8.clone.control.EXIT_MARKER
import com.fly8.clone.control.withExitMarker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShellMarkerTest {
    private fun sh(script: String): String {
        val p = ProcessBuilder("sh", "-c", script).redirectErrorStream(true).start()
        val out = p.inputStream.bufferedReader().readText()
        p.waitFor()
        return out
    }

    /** The control server launch line ends in `&`; `cmd &; echo` used to be a syntax error. */
    @Test fun backgroundCommandStillReportsExitCode() {
        val out = sh(withExitMarker("true >/dev/null 2>&1 &"))
        assertTrue(out, out.trim().endsWith("${EXIT_MARKER}0"))
    }

    @Test fun foregroundExitCodeIsReported() {
        assertEquals("${EXIT_MARKER}3", sh(withExitMarker("(exit 3)")).trim())
    }
}
