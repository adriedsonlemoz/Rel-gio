package com.example.relogioflutuante.timezones

import org.junit.Assert.assertEquals
import org.junit.Test

class WorldClockListRulesTest {
    @Test
    fun addDoesNotDuplicateZone() {
        val current = listOf("UTC")
        assertEquals(current, WorldClockListRules.add(current, "UTC"))
        assertEquals(listOf("UTC", "Asia/Tokyo"), WorldClockListRules.add(current, "Asia/Tokyo"))
    }

    @Test
    fun removeAndMovePreserveOrder() {
        val current = listOf("UTC", "Asia/Tokyo", "Europe/London")
        assertEquals(
            listOf("Asia/Tokyo", "UTC", "Europe/London"),
            WorldClockListRules.move(current, 1, -1)
        )
        assertEquals(
            listOf("UTC", "Europe/London"),
            WorldClockListRules.remove(current, "Asia/Tokyo")
        )
    }

    @Test
    fun invalidMoveLeavesListUnchanged() {
        val current = listOf("UTC", "Asia/Tokyo")
        assertEquals(current, WorldClockListRules.move(current, 0, -1))
        assertEquals(current, WorldClockListRules.move(current, 1, 1))
    }
}
