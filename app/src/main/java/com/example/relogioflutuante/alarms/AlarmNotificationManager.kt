package com.example.relogioflutuante.alarms

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.example.relogioflutuante.MainActivity
import com.example.relogioflutuante.R
import java.util.Locale

class AlarmNotificationManager(private val context: Context) {
    private val manager = context.getSystemService(NotificationManager::class.java)

    fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val ringChannel = NotificationChannel(
            RING_CHANNEL_ID,
            "Alarmes ativos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Mostra um alarme enquanto ele está tocando."
            setSound(null, null)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val fallbackChannel = NotificationChannel(
            FALLBACK_CHANNEL_ID,
            "Alertas de alarme",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Emite o alerta quando o Android impede o toque contínuo."
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            setSound(uri, attributes)
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannels(listOf(ringChannel, fallbackChannel))
    }

    fun buildRinging(alarm: Alarm): Notification = Notification.Builder(context, RING_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_nav_alarm)
        .setContentTitle(alarmTitle(alarm))
        .setContentText("Alarme de ${formatTime(alarm)}")
        .setCategory(Notification.CATEGORY_ALARM)
        .setVisibility(Notification.VISIBILITY_PUBLIC)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setContentIntent(openAppIntent())
        .addAction(stopAction())
        .build()

    fun postFallback(alarm: Alarm) {
        createChannels()
        val notification = Notification.Builder(context, FALLBACK_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nav_alarm)
            .setContentTitle(alarmTitle(alarm))
            .setContentText("Alarme de ${formatTime(alarm)}")
            .setCategory(Notification.CATEGORY_ALARM)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent())
            .build()
        manager.notify(FALLBACK_NOTIFICATION_ID + alarm.id.toInt(), notification)
    }

    private fun stopAction(): Notification.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            STOP_REQUEST_CODE,
            Intent(context, AlarmRingingService::class.java)
                .setAction(AlarmRingingService.ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Parar",
            pendingIntent
        ).build()
    }

    private fun openAppIntent(): PendingIntent = PendingIntent.getActivity(
        context,
        OPEN_REQUEST_CODE,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun alarmTitle(alarm: Alarm): String = alarm.label.ifBlank { "Alarme" }

    private fun formatTime(alarm: Alarm): String = String.format(
        Locale.getDefault(),
        "%02d:%02d",
        alarm.hour,
        alarm.minute
    )

    companion object {
        const val RING_CHANNEL_ID = "alarm_ringing"
        const val SERVICE_NOTIFICATION_ID = 22_100
        private const val FALLBACK_CHANNEL_ID = "alarm_fallback"
        private const val FALLBACK_NOTIFICATION_ID = 22_500
        private const val STOP_REQUEST_CODE = 22_101
        private const val OPEN_REQUEST_CODE = 22_102
    }
}
