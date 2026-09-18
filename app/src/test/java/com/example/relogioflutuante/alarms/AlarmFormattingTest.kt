package com.example.relogioflutuante.alarms

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmFormattingTest {
    @Test
    fun summarizesCommonRepeatPatterns() {
        assertEquals("Uma vez", AlarmFormatting.repeatSummary(emptySet()))
        assertEquals(
            "Seg a sex",
            AlarmFormatting.repeatSummary(
                setOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY
                )
            )
        )
        assertEquals(
            "Fim de semana",
            AlarmFormatting.repeatSummary(setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
        )
    }

    @Test
    fun reportsTimeRemainingToNextTrigger() {
        val zone = ZoneId.of("America/Sao_Paulo")
        val now = Instant.parse("2026-09-18T12:00:00Z").toEpochMilli()
        val alarm = Alarm(1, 11, 37)

        assertEquals("Hoje · em 2h 37min", AlarmFormatting.nextTriggerSummary(alarm, now, zone))
    }

    @Test
    fun disabledAlarmReportsDisabled() {
        assertEquals("Desativado", AlarmFormatting.nextTriggerSummary(Alarm(1, 10, 0, enabled = false), 0L))
    }
}
