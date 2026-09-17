package com.example.relogioflutuante.overlay

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.relogioflutuante.state.ClockState
import com.example.relogioflutuante.state.CountdownState
import com.example.relogioflutuante.state.OverlayMode
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.state.formatDuration

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var notifications: OverlayNotificationManager
    private var overlayView: View? = null
    private var timeText: TextView? = null
    private var statusText: TextView? = null
    private var params: WindowManager.LayoutParams? = null
    private val handler = Handler(Looper.getMainLooper())
    private var lastFinishedState = false

    private val ticker = object : Runnable {
        override fun run() {
            updateOverlay()
            handler.postDelayed(this, 200L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        notifications = OverlayNotificationManager(this).also { it.createChannel() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (overlayView == null) addOverlay()
        startForegroundCompat()
        OverlayState.setEnabled(this, true)
        handler.removeCallbacks(ticker)
        handler.post(ticker)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun addOverlay() {
        val binding = OverlayViewFactory(this).create(onClose = { stopSelf() })
        timeText = binding.timeText
        statusText = binding.statusText
        overlayView = binding.root

        val (savedX, savedY) = OverlayState.position(this)
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = savedX
            y = savedY
        }

        binding.root.setOnTouchListener(
            OverlayDragTouchListener(this, windowManager) { params }
        )
        windowManager.addView(binding.root, params)
        updateOverlay()
    }

    private fun updateOverlay() {
        when (OverlayState.mode(this)) {
            OverlayMode.CLOCK -> showClock()
            OverlayMode.COUNTDOWN -> showCountdown()
        }
    }

    private fun showClock() {
        timeText?.text = ClockState.formattedTime(this)
        statusText?.visibility = View.GONE
        lastFinishedState = false
    }

    private fun showCountdown() {
        val snapshot = CountdownState.snapshot(this)
        timeText?.text = formatDuration(snapshot.remainingMillis)
        if (snapshot.isFinished) {
            statusText?.apply {
                text = "TEMPO ESGOTADO"
                setTextColor(Color.rgb(248, 113, 113))
                visibility = View.VISIBLE
            }
            if (!lastFinishedState) notifications.notifyTimeFinished(NOTIFICATION_ID)
            lastFinishedState = true
        } else {
            statusText?.visibility = View.GONE
            lastFinishedState = false
        }
    }

    private fun startForegroundCompat() {
        val notification = notifications.build()
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

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        overlayView?.let { view -> runCatching { windowManager.removeView(view) } }
        overlayView = null
        OverlayState.setEnabled(this, false)
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP = "com.example.relogioflutuante.action.STOP_OVERLAY"
        private const val NOTIFICATION_ID = 101
    }
}
