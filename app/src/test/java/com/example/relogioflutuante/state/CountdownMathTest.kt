package com.example.relogioflutuante.state

import org.junit.Assert.assertEquals
import org.junit.Test

class CountdownMathTest {
    @Test
    fun remainingNeverGoesNegative() {
        assertEquals(0L, CountdownMath.remainingMillis(1_000L, 1_500L))
    }

    @Test
    fun remainingUsesElapsedDifference() {
        assertEquals(3_250L, CountdownMath.remainingMillis(8_250L, 5_000L))
    }

    @Test
    fun delayAlignsWithNextSecond() {
        assertEquals(750L, CountdownMath.delayUntilNextSecond(10_250L))
        assertEquals(1_000L, CountdownMath.delayUntilNextSecond(10_000L))
    }
}
