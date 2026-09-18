package com.example.relogioflutuante.alarms

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class AlarmFormattingTest {
    @Test
    fun summarizesCommonRepeatPatterns() {
        assertEquals("Uma vez", AlarmFormatting.repeatSummary(emptySet()))
        assertEquals(
            "Seg a sex",
            AlarmFormatting.repeatSummary(
                setOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY
                )
            )
        )
        assertEquals(
            "Fim de semana",
            AlarmFormatting.repeatSummary(setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
        )
    }
}
