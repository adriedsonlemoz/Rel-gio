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
                enabled = true,
                zoneId = "America/Sao_Paulo",
                sound = AlarmSound.NOTIFICATION,
                vibrate = false,
                snoozeMinutes = 15
            ),
            Alarm(id = 9L, hour = 20, minute = 10, enabled = false)
        )

        assertEquals(alarms, AlarmCodec.decode(AlarmCodec.encode(alarms)))
    }


    @Test
    fun legacyAlarmWithoutZoneStillLoads() {
        val decoded = AlarmCodec.decode("1:7:30:1:127:VGVzdGU")

        assertEquals(1, decoded.size)
        assertEquals(null, decoded.first().zoneId)
        assertEquals("Teste", decoded.first().label)
    }


    @Test
    fun legacyAlarmGetsDefaultSoundVibrationAndSnooze() {
        val alarm = AlarmCodec.decode("1:7:30:1:127:VGVzdGU").single()

        assertEquals(AlarmSound.ALARM, alarm.sound)
        assertEquals(true, alarm.vibrate)
        assertEquals(5, alarm.snoozeMinutes)
    }

    @Test
    fun invalidEntriesAreIgnored() {
        val decoded = AlarmCodec.decode("inválido;1:25:00:1:0:")

        assertEquals(emptyList<Alarm>(), decoded)
    }
}
