package com.example.relogioflutuante.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.example.relogioflutuante.MainActivity

class AlarmScheduler(context: Context) {
    private val appContext = context.applicationContext
    private val alarmManager = appContext.getSystemService(AlarmManager::class.java)

    fun canScheduleExact(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    fun schedule(alarm: Alarm) {
        cancel(alarm.id)
        if (!alarm.enabled) return
        val triggerAt = AlarmTimeRules.nextTriggerMillis(alarm, System.currentTimeMillis())
        val operation = fireIntent(alarm.id)
        if (canScheduleExact()) {
            val showIntent = PendingIntent.getActivity(
                appContext,
                SHOW_REQUEST_CODE,
                Intent(appContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerAt, showIntent),
                operation
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                operation
            )
        }
    }


    fun scheduleSnooze(alarm: Alarm, minutes: Int = alarm.snoozeMinutes) {
        if (minutes <= 0) return
        val triggerAt = System.currentTimeMillis() + minutes * 60_000L
        val operation = snoozeIntent(alarm.id)
        if (canScheduleExact()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                operation
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                operation
            )
        }
    }

    fun cancel(id: Long) {
        alarmManager.cancel(fireIntent(id))
        alarmManager.cancel(snoozeIntent(id))
    }

    fun rescheduleAll() {
        AlarmRepository(appContext).all().filter { it.enabled }.forEach(::schedule)
    }

    fun exactAlarmSettingsIntent(): Intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
        data = Uri.parse("package:${appContext.packageName}")
    }

    private fun fireIntent(id: Long): PendingIntent = PendingIntent.getBroadcast(
        appContext,
        requestCode(id),
        Intent(appContext, AlarmReceiver::class.java)
            .setAction(AlarmReceiver.ACTION_FIRE)
            .putExtra(AlarmReceiver.EXTRA_ALARM_ID, id),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun snoozeIntent(id: Long): PendingIntent = PendingIntent.getBroadcast(
        appContext,
        requestCode(id) xor SNOOZE_REQUEST_MASK,
        Intent(appContext, AlarmReceiver::class.java)
            .setAction(AlarmReceiver.ACTION_SNOOZE_FIRE)
            .putExtra(AlarmReceiver.EXTRA_ALARM_ID, id),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun requestCode(id: Long): Int = (id xor (id ushr 32)).toInt()

    private companion object {
        const val SHOW_REQUEST_CODE = 71_001
        const val SNOOZE_REQUEST_MASK = 0x5A5A0000
    }
}
