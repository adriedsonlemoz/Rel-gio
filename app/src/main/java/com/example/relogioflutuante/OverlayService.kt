package com.example.relogioflutuante

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
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
        createNotificationChannel()
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
        startForegroundCompat(buildNotification())
        OverlayState.setEnabled(this, true)

        handler.removeCallbacks(ticker)
        handler.post(ticker)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun addOverlay() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(8), dp(8), dp(8))
            background = roundedBackground(Color.argb(238, 17, 24, 39), dp(18).toFloat())
            elevation = dp(8).toFloat()
        }

        val textColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        timeText = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 24f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            includeFontPadding = false
            letterSpacing = 0.04f
        }

        statusText = TextView(this).apply {
            setTextColor(Color.rgb(148, 163, 184))
            textSize = 10f
            includeFontPadding = false
            visibility = View.GONE
        }

        textColumn.addView(timeText)
        textColumn.addView(statusText)

        val close = TextView(this).apply {
            text = "×"
            setTextColor(Color.rgb(203, 213, 225))
            textSize = 24f
            gravity = Gravity.CENTER
            setPadding(dp(12), 0, dp(6), dp(2))
            setOnClickListener { stopSelf() }
            contentDescription = "Fechar relógio flutuante"
        }

        root.addView(textColumn, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        root.addView(close, LinearLayout.LayoutParams(dp(44), LinearLayout.LayoutParams.MATCH_PARENT))

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

        root.setOnTouchListener(DragTouchListener())
        overlayView = root
        windowManager.addView(root, params)
        updateOverlay()
    }

    private fun updateOverlay() {
        val mode = OverlayState.mode(this)
        when (mode) {
            OverlayMode.CLOCK -> {
                timeText?.text = ClockState.formattedTime(this)
                statusText?.visibility = View.GONE
                lastFinishedState = false
            }

            OverlayMode.COUNTDOWN -> {
                val snapshot = CountdownState.snapshot(this)
                timeText?.text = formatDuration(snapshot.remainingMillis)
                if (snapshot.isFinished) {
                    statusText?.apply {
                        text = "TEMPO ESGOTADO"
                        setTextColor(Color.rgb(248, 113, 113))
                        visibility = View.VISIBLE
                    }
                    if (!lastFinishedState) {
                        val manager = getSystemService(NotificationManager::class.java)
                        manager.notify(NOTIFICATION_ID, buildNotification("Tempo esgotado — 00:00:00"))
                    }
                    lastFinishedState = true
                } else {
                    statusText?.visibility = View.GONE
                    lastFinishedState = false
                }
            }
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        overlayView?.let { view ->
            runCatching { windowManager.removeView(view) }
        }
        overlayView = null
        OverlayState.setEnabled(this, false)
        super.onDestroy()
    }

    private inner class DragTouchListener : View.OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var touchX = 0f
        private var touchY = 0f
        private var moved = false

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val lp = params ?: return false
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = lp.x
                    initialY = lp.y
                    touchX = event.rawX
                    touchY = event.rawY
                    moved = false
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchX).roundToInt()
                    val dy = (event.rawY - touchY).roundToInt()
                    if (kotlin.math.abs(dx) > dp(2) || kotlin.math.abs(dy) > dp(2)) moved = true

                    val maxX = (resources.displayMetrics.widthPixels - view.width).coerceAtLeast(0)
                    val maxY = (resources.displayMetrics.heightPixels - view.height).coerceAtLeast(0)
                    lp.x = (initialX + dx).coerceIn(0, maxX)
                    lp.y = (initialY + dy).coerceIn(0, maxY)
                    windowManager.updateViewLayout(view, lp)
                    return true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    OverlayState.savePosition(this@OverlayService, lp.x, lp.y)
                    return true
                }
            }
            return false
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.overlay_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.overlay_channel_description)
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(content: String? = null): Notification {
        val openIntent = Intent(this, MainActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            this,
            1,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, OverlayService::class.java).setAction(ACTION_STOP)
        val stopPendingIntent = PendingIntent.getService(
            this,
            2,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val modeLabel = when (OverlayState.mode(this)) {
            OverlayMode.CLOCK -> "Relógio sobre outros apps ativo"
            OverlayMode.COUNTDOWN -> "Contagem sobre outros apps ativa"
        }

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Relógio Flutuante")
            .setContentText(content ?: modeLabel)
            .setOngoing(true)
            .setContentIntent(openPendingIntent)
            .addAction(Notification.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel, "Desativar", stopPendingIntent).build())
            .build()
    }

    private fun startForegroundCompat(notification: Notification) {
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

    private fun roundedBackground(color: Int, radius: Float) = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(color)
        cornerRadius = radius
        setStroke(dp(1), Color.argb(90, 100, 116, 139))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()

    companion object {
        const val ACTION_STOP = "com.example.relogioflutuante.action.STOP_OVERLAY"
        private const val CHANNEL_ID = "floating_clock_overlay"
        private const val NOTIFICATION_ID = 101
    }
}
