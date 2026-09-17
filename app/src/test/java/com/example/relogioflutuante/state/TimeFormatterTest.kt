package com.example.relogioflutuante.state

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {
    @Test
    fun formatsHoursMinutesAndSeconds() {
        assertEquals("01:02:03", formatDuration(3_723_000L))
    }

    @Test
    fun roundsDisplayedSecondUpWhileTimeRemains() {
        assertEquals("00:00:01", formatDuration(1L))
    }
}
