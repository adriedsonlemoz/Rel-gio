package com.example.relogioflutuante.alarms

import java.time.DayOfWeek
import java.util.Locale

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

    fun shortDay(day: DayOfWeek): String = when (day) {
        DayOfWeek.MONDAY -> "Seg"
        DayOfWeek.TUESDAY -> "Ter"
        DayOfWeek.WEDNESDAY -> "Qua"
        DayOfWeek.THURSDAY -> "Qui"
        DayOfWeek.FRIDAY -> "Sex"
        DayOfWeek.SATURDAY -> "Sáb"
        DayOfWeek.SUNDAY -> "Dom"
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
