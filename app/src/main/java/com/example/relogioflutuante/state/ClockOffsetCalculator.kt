package com.example.relogioflutuante.state

import java.time.Duration
import java.time.LocalTime

object ClockOffsetCalculator {
    fun offsetMillis(now: LocalTime, target: LocalTime): Long =
        Duration.between(now, target).toMillis()
}
