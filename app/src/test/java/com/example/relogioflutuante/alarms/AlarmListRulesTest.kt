package com.example.relogioflutuante.alarms

import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmListRulesTest {
    @Test
    fun activeAlarmsAreSortedByNextTriggerAndDisabledComeLast() {
        val zone = ZoneId.of("America/Sao_Paulo")
        val now = ZonedDateTime.of(2026, 9, 18, 14, 0, 0, 0, zone).toInstant().toEpochMilli()
        val daily = DayOfWeek.entries.toSet()
        val alarms = listOf(
            Alarm(1, 19, 55, "Zulanka", daily, true, zone.id),
            Alarm(2, 15, 55, "Corvith", daily, true, zone.id),
            Alarm(3, 18, 55, "Ortson", daily, false, zone.id)
        )

        val sorted = AlarmListRules.sortedByNextTrigger(alarms, now)

        assertEquals(listOf(2L, 1L, 3L), sorted.map { it.id })
    }
}
