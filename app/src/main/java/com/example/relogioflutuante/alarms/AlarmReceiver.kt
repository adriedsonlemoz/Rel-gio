package com.example.relogioflutuante.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_FIRE) return
        val id = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        val repository = AlarmRepository(context)
        val alarm = repository.find(id) ?: return
        if (!alarm.enabled) return

        if (alarm.isRepeating) {
            AlarmScheduler(context).schedule(alarm)
        } else {
            repository.setEnabled(alarm.id, false)
        }

        val ringIntent = Intent(context, AlarmRingingService::class.java)
            .setAction(AlarmRingingService.ACTION_RING)
            .putExtra(AlarmRingingService.EXTRA_ALARM_ID, alarm.id)
            .putExtra(AlarmRingingService.EXTRA_HOUR, alarm.hour)
            .putExtra(AlarmRingingService.EXTRA_MINUTE, alarm.minute)
            .putExtra(AlarmRingingService.EXTRA_LABEL, alarm.label)

        runCatching {
            ContextCompat.startForegroundService(context, ringIntent)
        }.onFailure {
            AlarmNotificationManager(context).postFallback(alarm)
        }
    }

    companion object {
        const val ACTION_FIRE = "com.example.relogioflutuante.action.FIRE_ALARM"
        const val EXTRA_ALARM_ID = "alarm_id"
    }
}
