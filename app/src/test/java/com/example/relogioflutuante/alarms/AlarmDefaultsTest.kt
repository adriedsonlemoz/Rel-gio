package com.example.relogioflutuante.alarms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmDefaultsTest {
    @Test
    fun zyrvorthianDefaultsUseBrasiliaEveryDay() {
        val alarms = AlarmDefaults.definitions.mapIndexed { index, definition ->
            AlarmDefaults.create(index.toLong() + 1L, definition)
        }

        assertEquals(listOf("Zyrvorthian Corvith", "Zyrvorthian Ortson", "Zyrvorthian Zulanka"), alarms.map { it.label })
        assertEquals(listOf("16:55", "18:55", "19:55"), alarms.map { "%02d:%02d".format(it.hour, it.minute) })
        assertTrue(alarms.all { it.repeatDays.size == 7 })
        assertTrue(alarms.all { it.zoneId == AlarmDefaults.BRASILIA_ZONE })
    }
}
