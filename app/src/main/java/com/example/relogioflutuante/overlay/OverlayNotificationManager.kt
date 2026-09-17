package com.example.relogioflutuante.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.relogioflutuante.MainActivity
import com.example.relogioflutuante.R
import com.example.relogioflutuante.state.CountdownSnapshot
import com.example.relogioflutuante.state.OverlayMode
import com.example.relogioflutuante.state.OverlayState

class OverlayNotificationManager(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.overlay_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.overlay_channel_description)
                setShowBadge(false)
                enableVibration(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun build(content: String, countdown: CountdownSnapshot? = null): Notification {
        val builder = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(
                if (OverlayState.notificationOnly(context)) {
                    "Relógio Flutuante · modo compatível"
                } else {
                    "Relógio Flutuante"
                }
            )
            .setContentText(content)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setContentIntent(openAppIntent())
            .addAction(stopAction())

        if (OverlayState.mode(context) == OverlayMode.COUNTDOWN && countdown != null) {
            countdownAction(countdown)?.let(builder::addAction)
        }

        return builder.build()
    }

    fun update(notificationId: Int, content: String, countdown: CountdownSnapshot? = null) {
        notificationManager.notify(notificationId, build(content, countdown))
    }

    fun notifyTimeFinished(notificationId: Int) {
        notificationManager.notify(
            notificationId,
            build("Tempo esgotado · 00:00:00")
        )
    }

    private fun openAppIntent(): PendingIntent = PendingIntent.getActivity(
        context,
        1,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun stopAction(): Notification.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            2,
            Intent(context, OverlayService::class.java).setAction(OverlayService.ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Desativar",
            pendingIntent
        ).build()
    }

    private fun countdownAction(snapshot: CountdownSnapshot): Notification.Action? {
        val action = when {
            snapshot.isRunning -> OverlayService.ACTION_PAUSE_COUNTDOWN
            snapshot.remainingMillis > 0L && snapshot.remainingMillis < snapshot.configuredMillis ->
                OverlayService.ACTION_RESUME_COUNTDOWN
            snapshot.configuredMillis > 0L -> OverlayService.ACTION_START_COUNTDOWN
            else -> return null
        }
        val label = when (action) {
            OverlayService.ACTION_PAUSE_COUNTDOWN -> "Pausar"
            OverlayService.ACTION_RESUME_COUNTDOWN -> "Continuar"
            else -> "Iniciar"
        }
        val pendingIntent = PendingIntent.getService(
            context,
            3,
            Intent(context, OverlayService::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val icon = if (action == OverlayService.ACTION_PAUSE_COUNTDOWN) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        return Notification.Action.Builder(icon, label, pendingIntent).build()
    }

    companion object {
        private const val CHANNEL_ID = "floating_clock_overlay"
    }
}
