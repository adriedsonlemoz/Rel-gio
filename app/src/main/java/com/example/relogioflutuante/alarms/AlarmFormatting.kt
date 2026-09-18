package com.example.relogioflutuante.alarms

import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale
import kotlin.math.max

object AlarmFormatting {
    fun time(alarm: Alarm): String = String.format(
        Locale.getDefault(),
        "%02d:%02d",
        alarm.hour,
        alarm.minute
    )

    fun repeatSummary(days: Set<DayOfWeek>): String {
        if (days.isEmpty()) return "Uma vez"
        if (days.size == 7) return "Todos os dias"
        if (days == WEEKDAYS) return "Seg a sex"
        if (days == WEEKEND) return "Fim de semana"
        return DayOfWeek.entries.filter { it in days }.joinToString(" · ") { shortDay(it) }
    }

    fun nextTriggerSummary(
        alarm: Alarm,
        nowMillis: Long,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String {
        if (!alarm.enabled) return "Desativado"
        val triggerMillis = AlarmTimeRules.nextTriggerMillis(alarm, nowMillis, zoneId)
        val now = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(nowMillis), zoneId)
        val trigger = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(triggerMillis), zoneId)
        val day = when (trigger.toLocalDate().toEpochDay() - now.toLocalDate().toEpochDay()) {
            0L -> "Hoje"
            1L -> "Amanhã"
            else -> shortDay(trigger.dayOfWeek)
        }
        return "$day · em ${remainingDuration(nowMillis, triggerMillis)}"
    }

    fun remainingDuration(nowMillis: Long, triggerMillis: Long): String {
        var totalMinutes = max(0L, (triggerMillis - nowMillis + 59_999L) / 60_000L)
        val days = totalMinutes / (24L * 60L)
        totalMinutes %= 24L * 60L
        val hours = totalMinutes / 60L
        val minutes = totalMinutes % 60L
        return when {
            days > 0 && hours > 0 -> "${days}d ${hours}h"
            days > 0 -> "${days}d"
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}min"
            hours > 0 -> "${hours}h"
            else -> "${minutes.coerceAtLeast(1L)}min"
        }
    }

    fun shortDay(day: DayOfWeek): String = when (day) {
        DayOfWeek.MONDAY -> "Seg"
        DayOfWeek.TUESDAY -> "Ter"
        DayOfWeek.WEDNESDAY -> "Qua"
        DayOfWeek.THURSDAY -> "Qui"
        DayOfWeek.FRIDAY -> "Sex"
        DayOfWeek.SATURDAY -> "Sáb"
        DayOfWeek.SUNDAY -> "Dom"
    }

    fun compactDay(day: DayOfWeek): String = when (day) {
        DayOfWeek.MONDAY -> "S"
        DayOfWeek.TUESDAY -> "T"
        DayOfWeek.WEDNESDAY -> "Q"
        DayOfWeek.THURSDAY -> "Q"
        DayOfWeek.FRIDAY -> "S"
        DayOfWeek.SATURDAY -> "S"
        DayOfWeek.SUNDAY -> "D"
    }

    private val WEEKDAYS = setOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )
    private val WEEKEND = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
}
