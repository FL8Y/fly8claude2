package com.fly8.clone

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.fly8.clone.control.Controls
import com.fly8.clone.data.InstanceStore
import com.fly8.clone.data.LogStore
import com.fly8.clone.data.SettingsStore
import com.fly8.clone.session.DaemonService
import com.fly8.clone.session.NativeCloneBackend
import com.fly8.clone.session.SessionManager

/** Process-wide singletons. Local state loads before any network or control work begins. */
class Fly8 : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
        store = InstanceStore(this)
        logStore = LogStore(this)
        settingsStore = SettingsStore(this)
        sessions = SessionManager(this, NativeCloneBackend())
        Controls.init(this)
        createChannels()
    }

    private fun createChannels() {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_SESSIONS, "Running sessions", NotificationManager.IMPORTANCE_LOW)
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_PAIRING, "Wireless pairing", NotificationManager.IMPORTANCE_HIGH)
        )
    }

    companion object {
        const val CHANNEL_SESSIONS = "sessions"
        const val CHANNEL_PAIRING = "pairing"

        private lateinit var app: Fly8
        lateinit var store: InstanceStore
            private set
        lateinit var logStore: LogStore
            private set
        lateinit var settingsStore: SettingsStore
            private set
        lateinit var sessions: SessionManager
            private set

        val log get() = logStore
        val settings get() = settingsStore

        fun daemon(context: Context) = DaemonService.start(context.applicationContext)
    }
}
