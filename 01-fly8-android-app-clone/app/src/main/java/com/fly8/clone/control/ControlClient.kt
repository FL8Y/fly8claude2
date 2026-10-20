package com.fly8.clone.control

import android.util.Base64
import com.fly8.clone.server.ControlServer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

data class ExecResult(val code: Int, val output: String) {
    val ok get() = code == 0
}

/** One authenticated TCP channel to the local control server. */
class ControlClient(private val token: String) : AutoCloseable {
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var out: OutputStream? = null

    @Synchronized
    private fun ensure() {
        if (socket?.isConnected == true && socket?.isClosed == false) return
        close()
        val s = Socket()
        s.connect(InetSocketAddress("127.0.0.1", ControlServer.PORT), 1_500)
        s.tcpNoDelay = true
        val r = BufferedReader(InputStreamReader(s.getInputStream(), Charsets.UTF_8))
        val o = s.getOutputStream()
        o.write("AUTH $token\n".toByteArray()); o.flush()
        s.soTimeout = 3_000
        val ack = r.readLine()
        if (ack != "OK auth") { s.close(); error("Control server rejected FLY8 (token mismatch).") }
        socket = s; reader = r; out = o
    }

    /** Request/response. Throws on transport failure. */
    @Synchronized
    fun request(line: String, timeoutMs: Int = 5_000): String {
        ensure()
        return try {
            socket!!.soTimeout = timeoutMs
            out!!.write((line + "\n").toByteArray()); out!!.flush()
            reader!!.readLine() ?: error("Control server closed the connection.")
        } catch (t: Throwable) { close(); throw t }
    }

    /** Fire-and-forget (input events). */
    @Synchronized
    fun send(line: String) {
        try {
            ensure()
            out!!.write((line + "\n").toByteArray()); out!!.flush()
        } catch (t: Throwable) { close() }
    }

    fun ping(): Int? = runCatching {
        val r = request("PING", 2_000).split(' ')
        if (r[0] == "OK") r[1].toInt() else null
    }.getOrNull()

    /** Runs one allowlisted server operation (see server Ops). Arguments must not contain spaces. */
    fun op(name: String, vararg args: String, timeoutMs: Int = 70_000): ExecResult {
        require(args.none { it.isEmpty() || it.contains(' ') || it.contains('\n') }) { "invalid argument" }
        val r = request((listOf("OP", name) + args).joinToString(" "), timeoutMs).split(' ')
        return when (r[0]) {
            "OK" -> ExecResult(r[1].toInt(), String(Base64.decode(r.getOrElse(2) { "" }, Base64.NO_WRAP)).trim())
            else -> ExecResult(-1, String(Base64.decode(r.getOrElse(1) { "" }, Base64.NO_WRAP)))
        }
    }

    @Synchronized
    override fun close() {
        runCatching { socket?.close() }
        socket = null; reader = null; out = null
    }
}
