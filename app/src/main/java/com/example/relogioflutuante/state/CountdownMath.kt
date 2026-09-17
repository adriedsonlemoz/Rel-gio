package com.example.relogioflutuante.state

import kotlin.math.max

object CountdownMath {
    fun remainingMillis(endElapsedMillis: Long, nowElapsedMillis: Long): Long =
        max(0L, endElapsedMillis - nowElapsedMillis)

    fun delayUntilNextSecond(nowMillis: Long): Long =
        (1_000L - (nowMillis % 1_000L)).coerceIn(50L, 1_000L)
}
