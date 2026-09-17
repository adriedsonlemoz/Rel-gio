package com.example.relogioflutuante.overlay

import com.example.relogioflutuante.state.OverlayTimeFormat
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class OverlayTextFormatterTest {
    private val time = LocalTime.of(18, 34, 24)

    @Test
    fun formatsClockInAllDisplayModes() {
        assertEquals("18:34:24", OverlayTextFormatter.clock(time, OverlayTimeFormat.FULL))
        assertEquals("34:24", OverlayTextFormatter.clock(time, OverlayTimeFormat.MINUTES_SECONDS))
        assertEquals(":24", OverlayTextFormatter.clock(time, OverlayTimeFormat.SECONDS_ONLY))
    }

    @Test
    fun formatsCountdownInAllDisplayModes() {
        val millis = 3_724_000L
        assertEquals("01:02:04", OverlayTextFormatter.countdown(millis, OverlayTimeFormat.FULL))
        assertEquals("62:04", OverlayTextFormatter.countdown(millis, OverlayTimeFormat.MINUTES_SECONDS))
        assertEquals(":04", OverlayTextFormatter.countdown(millis, OverlayTimeFormat.SECONDS_ONLY))
    }
}
