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
import com.example.relogioflutuante.state.OverlayMode
import com.example.relogioflutuante.state.OverlayState

class OverlayNotificationManager(private val context: Context) {
    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.overlay_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.overlay_channel_description)
                setShowBadge(false)
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    fun build(content: String? = null): Notification {
        val openPendingIntent = PendingIntent.getActivity(
            context,
            1,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopPendingIntent = PendingIntent.getService(
            context,
            2,
            Intent(context, OverlayService::class.java).setAction(OverlayService.ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val modeLabel = when (OverlayState.mode(context)) {
            OverlayMode.CLOCK -> "Relógio sobre outros apps ativo"
            OverlayMode.COUNTDOWN -> "Contagem sobre outros apps ativa"
        }

        return Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Relógio Flutuante")
            .setContentText(content ?: modeLabel)
            .setOngoing(true)
            .setContentIntent(openPendingIntent)
            .addAction(
                Notification.Action.Builder(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    "Desativar",
                    stopPendingIntent
                ).build()
            )
            .build()
    }

    fun notifyTimeFinished(notificationId: Int) {
        context.getSystemService(NotificationManager::class.java)
            .notify(notificationId, build("Tempo esgotado — 00:00:00"))
    }

    companion object {
        private const val CHANNEL_ID = "floating_clock_overlay"
    }
}
