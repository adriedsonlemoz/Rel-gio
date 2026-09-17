package com.example.relogioflutuante.overlay

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import com.example.relogioflutuante.state.ClockState
import com.example.relogioflutuante.state.CountdownSnapshot
import com.example.relogioflutuante.state.CountdownState
import com.example.relogioflutuante.state.OverlayMode
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.state.formatDuration

class OverlayService : Service() {
    private lateinit var windowController: OverlayWindowController
    private lateinit var notifications: OverlayNotificationManager
    private val handler = Handler(Looper.getMainLooper())
    private var lastFinishedState = false
    private var lastNotificationContent: String? = null
    private var lastNotificationRunning: Boolean? = null

    private val ticker = object : Runnable {
        override fun run() {
            updatePresentation()
            scheduleNextTick()
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowController = OverlayWindowController(this) {
            OverlayState.setEnabled(this, false)
            stopSelf()
        }
        notifications = OverlayNotificationManager(this).also { it.createChannel() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                OverlayState.setEnabled(this, false)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_PAUSE_COUNTDOWN -> CountdownState.pause(this)
            ACTION_RESUME_COUNTDOWN -> CountdownState.resume(this)
            ACTION_START_COUNTDOWN -> CountdownState.start(this)
        }

        if (OverlayState.presentation(this) == OverlayPresentation.ACCESSIBILITY_OVERLAY) {
            stopSelf()
            return START_NOT_STICKY
        }

        OverlayState.setEnabled(this, true)
        startForegroundCompat(initialNotificationText())
        ensureCorrectPresentationMode()
        updatePresentation(force = true)
        handler.removeCallbacks(ticker)
        scheduleNextTick()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun ensureCorrectPresentationMode() {
        when (OverlayState.presentation(this)) {
            OverlayPresentation.SYSTEM_OVERLAY -> {
                if (!Settings.canDrawOverlays(this) || !windowController.ensureVisible()) {
                    OverlayState.setPresentation(this, OverlayPresentation.NOTIFICATION)
                    windowController.hide()
                }
            }
            OverlayPresentation.NOTIFICATION -> windowController.hide()
            OverlayPresentation.ACCESSIBILITY_OVERLAY -> windowController.hide()
        }
    }

    private fun updatePresentation(force: Boolean = false) {
        if (OverlayState.presentation(this) == OverlayPresentation.ACCESSIBILITY_OVERLAY) {
            stopSelf()
            return
        }
        ensureCorrectPresentationMode()
        when (OverlayState.mode(this)) {
            OverlayMode.CLOCK -> updateClock(force)
            OverlayMode.COUNTDOWN -> updateCountdown(force)
        }
    }

    private fun updateClock(force: Boolean) {
        val time = ClockState.formattedTime(this)
        if (OverlayState.presentation(this) == OverlayPresentation.SYSTEM_OVERLAY) {
            windowController.renderClock(time)
        }
        lastFinishedState = false
        updateNotificationIfNeeded(time, null, force)
    }

    private fun updateCountdown(force: Boolean) {
        val snapshot = CountdownState.snapshot(this)
        val time = formatDuration(snapshot.remainingMillis)
        if (OverlayState.presentation(this) == OverlayPresentation.SYSTEM_OVERLAY) {
            windowController.renderCountdown(time, snapshot.isFinished)
        }
        if (snapshot.isFinished && !lastFinishedState) {
            notifications.notifyTimeFinished(NOTIFICATION_ID)
        }
        lastFinishedState = snapshot.isFinished
        updateNotificationIfNeeded(time, snapshot, force)
    }

    private fun updateNotificationIfNeeded(
        liveContent: String,
        countdown: CountdownSnapshot?,
        force: Boolean
    ) {
        val content = if (OverlayState.presentation(this) == OverlayPresentation.NOTIFICATION) {
            liveContent
        } else {
            when (OverlayState.mode(this)) {
                OverlayMode.CLOCK -> "Janela de relógio ativa"
                OverlayMode.COUNTDOWN -> "Janela de contagem ativa"
            }
        }
        val running = countdown?.isRunning
        if (force || content != lastNotificationContent || running != lastNotificationRunning) {
            notifications.update(NOTIFICATION_ID, content, countdown)
            lastNotificationContent = content
            lastNotificationRunning = running
        }
    }

    private fun initialNotificationText(): String {
        if (OverlayState.presentation(this) == OverlayPresentation.SYSTEM_OVERLAY &&
            Settings.canDrawOverlays(this)
        ) {
            return when (OverlayState.mode(this)) {
                OverlayMode.CLOCK -> "Janela de relógio ativa"
                OverlayMode.COUNTDOWN -> "Janela de contagem ativa"
            }
        }
        return when (OverlayState.mode(this)) {
            OverlayMode.CLOCK -> ClockState.formattedTime(this)
            OverlayMode.COUNTDOWN -> formatDuration(CountdownState.snapshot(this).remainingMillis)
        }
    }

    private fun startForegroundCompat(content: String) {
        val countdown = if (OverlayState.mode(this) == OverlayMode.COUNTDOWN) {
            CountdownState.snapshot(this)
        } else null
        val notification = notifications.build(content, countdown)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun scheduleNextTick() {
        handler.removeCallbacks(ticker)
        val now = System.currentTimeMillis()
        val delay = (1_000L - (now % 1_000L)).coerceIn(50L, 1_000L)
        handler.postDelayed(ticker, delay)
    }

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        windowController.hide()
        if (OverlayState.presentation(this) != OverlayPresentation.ACCESSIBILITY_OVERLAY) {
            OverlayState.setEnabled(this, false)
        }
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP = "com.example.relogioflutuante.action.STOP_OVERLAY"
        const val ACTION_PAUSE_COUNTDOWN = "com.example.relogioflutuante.action.PAUSE_COUNTDOWN"
        const val ACTION_RESUME_COUNTDOWN = "com.example.relogioflutuante.action.RESUME_COUNTDOWN"
        const val ACTION_START_COUNTDOWN = "com.example.relogioflutuante.action.START_COUNTDOWN"
        private const val NOTIFICATION_ID = 101
    }
}
