package com.example.relogioflutuante.timezones

import android.content.Context
import com.example.relogioflutuante.state.appPreferences

class WorldClockRepository(context: Context) {
    private val prefs = context.applicationContext.appPreferences()

    fun loadZoneIds(): List<String> {
        val raw = prefs.getString(KEY_ZONE_IDS, "").orEmpty()
        if (raw.isBlank()) return emptyList()
        return raw.split(SEPARATOR)
            .map(String::trim)
            .filter { it.isNotEmpty() && WorldClockCatalog.find(it) != null }
            .distinct()
    }

    fun saveZoneIds(zoneIds: List<String>) {
        val sanitized = zoneIds.filter { WorldClockCatalog.find(it) != null }.distinct()
        prefs.edit().putString(KEY_ZONE_IDS, sanitized.joinToString(SEPARATOR)).apply()
    }

    private companion object {
        const val KEY_ZONE_IDS = "world_clock_zone_ids"
        const val SEPARATOR = "|"
    }
}
