package com.example.relogioflutuante.timezones

import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class WorldClockTimeTest {
    @Test
    fun displaysTokyoRelativeToSaoPaulo() {
        val display = WorldClockTime.display(
            zoneId = ZoneId.of("Asia/Tokyo"),
            instant = Instant.parse("2026-01-01T12:00:00Z"),
            localZone = ZoneId.of("America/Sao_Paulo")
        )

        assertEquals("21:00:00", display.time)
        assertEquals("UTC+09", display.utcOffset)
        assertEquals("+12h", display.relativeOffset)
        assertEquals("12h à frente", display.relativeDescription)
    }

    @Test
    fun supportsHalfHourOffsets() {
        assertEquals("UTC+05:30", WorldClockTime.formatUtcOffset(19_800))
        assertEquals("+5h30", WorldClockTime.formatDifference(19_800))
        assertEquals("5h 30min à frente", WorldClockTime.formatRelativeDescription(19_800))
    }

    @Test
    fun equalOffsetIsReportedAsSameTime() {
        assertEquals("mesma hora", WorldClockTime.formatDifference(0))
        assertEquals("mesma hora", WorldClockTime.formatRelativeDescription(0))
        assertEquals("UTC", WorldClockTime.formatUtcOffset(0))
    }
}
