package com.example.relogioflutuante.alarms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmDefaultsTest {
    @Test
    fun corvithUsesCorrectBrasiliaTime() {
        val corvith = AlarmDefaults.definitions.first { it.label == "Zyrvorthian Corvith" }
        assertEquals(15, corvith.hour)
        assertEquals(55, corvith.minute)
    }

    @Test
    fun defaultAlarmUsesBrasiliaZoneAndRepeatsEveryDay() {
        val alarm = AlarmDefaults.create(1L, AlarmDefaults.definitions.first())
        assertEquals(AlarmDefaults.BRASILIA_ZONE, alarm.zoneId)
        assertEquals(7, alarm.repeatDays.size)
        assertTrue(alarm.enabled)
    }
}
