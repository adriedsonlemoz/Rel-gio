package com.example.relogioflutuante.state

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ClockOffsetCalculatorTest {
    @Test
    fun calculatesPositiveOffset() {
        val now = LocalTime.of(10, 0, 0)
        val target = LocalTime.of(10, 0, 15)
        assertEquals(15_000L, ClockOffsetCalculator.offsetMillis(now, target))
    }

    @Test
    fun calculatesNegativeOffset() {
        val now = LocalTime.of(10, 0, 15)
        val target = LocalTime.of(10, 0, 0)
        assertEquals(-15_000L, ClockOffsetCalculator.offsetMillis(now, target))
    }
}
