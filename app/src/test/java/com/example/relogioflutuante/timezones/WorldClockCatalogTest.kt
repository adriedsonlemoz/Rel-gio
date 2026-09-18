package com.example.relogioflutuante.timezones

import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WorldClockCatalogTest {
    @Test
    fun catalogUsesValidUniqueZoneIds() {
        val ids = WorldClockCatalog.entries.map { it.zoneId }
        assertEquals(ids.size, ids.distinct().size)
        ids.forEach { assertNotNull(ZoneId.of(it)) }
    }

    @Test
    fun searchMatchesCityCountryAndZoneId() {
        assertEquals("Asia/Tokyo", WorldClockCatalog.search("toquio").single().zoneId)
        assertEquals("Europe/Lisbon", WorldClockCatalog.search("Portugal").single().zoneId)
        assertEquals("America/New_York", WorldClockCatalog.search("New_York").single().zoneId)
    }
}
