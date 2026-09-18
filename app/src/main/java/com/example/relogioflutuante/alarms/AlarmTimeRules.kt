package com.example.relogioflutuante.alarms

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object AlarmTimeRules {
    fun nextTriggerMillis(
        alarm: Alarm,
        nowMillis: Long,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Long {
        val now = Instant.ofEpochMilli(nowMillis).atZone(zoneId)
        val time = LocalTime.of(alarm.hour, alarm.minute)
        val candidate = if (alarm.repeatDays.isEmpty()) {
            nextOneShot(now, time)
        } else {
            nextRepeating(now, time, alarm)
        }
        return candidate.toInstant().toEpochMilli()
    }

    private fun nextOneShot(now: ZonedDateTime, time: LocalTime): ZonedDateTime {
        val today = now.toLocalDate().atTime(time).atZone(now.zone)
        return if (today.isAfter(now)) today else today.plusDays(1)
    }

    private fun nextRepeating(
        now: ZonedDateTime,
        time: LocalTime,
        alarm: Alarm
    ): ZonedDateTime {
        for (daysAhead in 0..7) {
            val date = now.toLocalDate().plusDays(daysAhead.toLong())
            if (date.dayOfWeek !in alarm.repeatDays) continue
            val candidate = date.atTime(time).atZone(now.zone)
            if (candidate.isAfter(now)) return candidate
        }
        error("Não foi possível calcular o próximo alarme")
    }
}
