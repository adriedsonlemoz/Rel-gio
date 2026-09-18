package com.example.relogioflutuante.alarms

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class AlarmCodecTest {
    @Test
    fun roundTripPreservesAlarmData() {
        val alarms = listOf(
            Alarm(
                id = 8L,
                hour = 6,
                minute = 45,
                label = "Acordar: café ☕",
                repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                enabled = true
            ),
            Alarm(id = 9L, hour = 20, minute = 10, enabled = false)
        )

        assertEquals(alarms, AlarmCodec.decode(AlarmCodec.encode(alarms)))
    }

    @Test
    fun invalidEntriesAreIgnored() {
        val decoded = AlarmCodec.decode("inválido;1:25:00:1:0:")

        assertEquals(emptyList<Alarm>(), decoded)
    }
}
