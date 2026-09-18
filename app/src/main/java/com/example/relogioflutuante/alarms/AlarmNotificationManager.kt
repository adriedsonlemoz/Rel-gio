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
        manager.createNotificationChannel(ringChannel)
    }

    fun buildRinging(alarm: Alarm): Notification {
        val builder = Notification.Builder(context, RING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nav_alarm)
            .setContentTitle(alarmTitle(alarm))
            .setContentText("Alarme de ${formatTime(alarm)}")
            .setCategory(Notification.CATEGORY_ALARM)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openAppIntent())
        if (alarm.snoozeMinutes > 0) builder.addAction(snoozeAction(alarm))
        builder.addAction(stopAction(alarm.id))
        return builder.build()
    }

    fun postFallback(alarm: Alarm) {
        createChannels()
        val channelId = ensureFallbackChannel(alarm)
        val builder = Notification.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_nav_alarm)
            .setContentTitle(alarmTitle(alarm))
            .setContentText("Alarme de ${formatTime(alarm)}")
            .setCategory(Notification.CATEGORY_ALARM)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent())
        if (alarm.snoozeMinutes > 0) builder.addAction(snoozeAction(alarm))
        builder.addAction(stopAction(alarm.id))
        manager.notify(fallbackNotificationId(alarm.id), builder.build())
    }

    fun cancelFallback(alarmId: Long) {
        if (alarmId < 0) return
        manager.cancel(fallbackNotificationId(alarmId))
    }

    private fun ensureFallbackChannel(alarm: Alarm): String {
        val id = "alarm_fallback_${alarm.sound.storageKey}_${if (alarm.vibrate) 1 else 0}"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return id
        val channel = NotificationChannel(
            id,
            "Alertas de alarme",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Emite o alerta quando o Android impede o toque contínuo."
            val type = ringtoneType(alarm.sound)
            if (type == null) {
                setSound(null, null)
            } else {
                val uri = RingtoneManager.getDefaultUri(type)
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setSound(uri, attributes)
            }
            enableVibration(alarm.vibrate)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(channel)
        return id
    }

    private fun ringtoneType(sound: AlarmSound): Int? = when (sound) {
        AlarmSound.ALARM -> RingtoneManager.TYPE_ALARM
        AlarmSound.RINGTONE -> RingtoneManager.TYPE_RINGTONE
        AlarmSound.NOTIFICATION -> RingtoneManager.TYPE_NOTIFICATION
        AlarmSound.SILENT -> null
    }

    private fun snoozeAction(alarm: Alarm): Notification.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            actionRequestCode(alarm.id, SNOOZE_REQUEST_SALT),
            Intent(context, AlarmRingingService::class.java)
                .setAction(AlarmRingingService.ACTION_SNOOZE)
                .putExtra(AlarmRingingService.EXTRA_ALARM_ID, alarm.id),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Action.Builder(
            android.R.drawable.ic_lock_idle_alarm,
            "Soneca ${alarm.snoozeMinutes} min",
            pendingIntent
        ).build()
    }

    private fun stopAction(alarmId: Long): Notification.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            actionRequestCode(alarmId, STOP_REQUEST_SALT),
            Intent(context, AlarmRingingService::class.java)
                .setAction(AlarmRingingService.ACTION_STOP)
                .putExtra(AlarmRingingService.EXTRA_ALARM_ID, alarmId),
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

    private fun actionRequestCode(id: Long, salt: Int): Int =
        ((id xor (id ushr 32)).toInt()) xor salt

    private fun fallbackNotificationId(id: Long): Int =
        FALLBACK_NOTIFICATION_BASE + ((id xor (id ushr 32)).toInt() and 0x0FFF)

    companion object {
        const val RING_CHANNEL_ID = "alarm_ringing"
        const val SERVICE_NOTIFICATION_ID = 22_100
        private const val FALLBACK_NOTIFICATION_BASE = 22_500
        private const val STOP_REQUEST_SALT = 0x1100
        private const val SNOOZE_REQUEST_SALT = 0x2200
        private const val OPEN_REQUEST_CODE = 22_102
    }
}
