package com.example.relogioflutuante.ui.state

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.relogioflutuante.timezones.WorldClockCatalog
import com.example.relogioflutuante.timezones.WorldClockEntry
import com.example.relogioflutuante.timezones.WorldClockListRules
import com.example.relogioflutuante.timezones.WorldClockRepository

@Stable
class WorldClockUiState internal constructor(
    private val repository: WorldClockRepository,
    initialZoneIds: List<String>
) {
    var zoneIds by mutableStateOf(initialZoneIds)
        private set

    var showPicker by mutableStateOf(false)
        private set

    val entries: List<WorldClockEntry>
        get() = zoneIds.mapNotNull(WorldClockCatalog::find)

    fun openPicker() {
        showPicker = true
    }

    fun closePicker() {
        showPicker = false
    }

    fun add(zoneId: String) {
        update(WorldClockListRules.add(zoneIds, zoneId))
    }

    fun remove(zoneId: String) {
        update(WorldClockListRules.remove(zoneIds, zoneId))
    }

    fun moveUp(index: Int) {
        update(WorldClockListRules.move(zoneIds, index, -1))
    }

    fun moveDown(index: Int) {
        update(WorldClockListRules.move(zoneIds, index, 1))
    }

    private fun update(newValue: List<String>) {
        if (newValue == zoneIds) return
        zoneIds = newValue
        repository.saveZoneIds(newValue)
    }
}

@Composable
fun rememberWorldClockUiState(context: Context): WorldClockUiState {
    val appContext = context.applicationContext
    return remember(appContext) {
        val repository = WorldClockRepository(appContext)
        WorldClockUiState(repository, repository.loadZoneIds())
    }
}
