package com.example.relogioflutuante.alarms

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime

class AlarmTimeRulesTest {
    private val zone = ZoneId.of("UTC")

    @Test
    fun oneShotUsesTodayWhenTimeIsStillAhead() {
        val now = millis(2026, 9, 17, 8, 0)
        val alarm = Alarm(1, 9, 30)

        assertEquals(
            millis(2026, 9, 17, 9, 30),
            AlarmTimeRules.nextTriggerMillis(alarm, now, zone)
        )
    }

    @Test
    fun oneShotMovesToTomorrowWhenTimePassed() {
        val now = millis(2026, 9, 17, 22, 0)
        val alarm = Alarm(1, 7, 0)

        assertEquals(
            millis(2026, 9, 18, 7, 0),
            AlarmTimeRules.nextTriggerMillis(alarm, now, zone)
        )
    }

    @Test
    fun repeatingAlarmFindsNextSelectedWeekday() {
        val now = millis(2026, 9, 17, 22, 0)
        val alarm = Alarm(
            id = 1,
            hour = 7,
            minute = 15,
            repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
        )

        assertEquals(
            millis(2026, 9, 18, 7, 15),
            AlarmTimeRules.nextTriggerMillis(alarm, now, zone)
        )
    }

    private fun millis(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ): Long = ZonedDateTime.of(year, month, day, hour, minute, 0, 0, zone)
        .toInstant()
        .toEpochMilli()
}
