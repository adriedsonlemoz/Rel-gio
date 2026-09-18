package com.example.relogioflutuante.alarms

import android.content.Context
import com.example.relogioflutuante.state.appPreferences

class AlarmRepository(context: Context) {
    private val prefs = context.applicationContext.appPreferences()

    fun all(): List<Alarm> = AlarmCodec.decode(prefs.getString(KEY_ALARMS, null))

    fun find(id: Long): Alarm? = all().firstOrNull { it.id == id }

    fun nextId(): Long {
        val current = prefs.getLong(KEY_NEXT_ID, 1L)
        prefs.edit().putLong(KEY_NEXT_ID, current + 1L).apply()
        return current
    }

    fun save(alarm: Alarm) {
        val updated = all().filterNot { it.id == alarm.id } + alarm
        write(updated)
    }

    fun delete(id: Long) {
        write(all().filterNot { it.id == id })
    }

    fun setEnabled(id: Long, enabled: Boolean): Alarm? {
        val existing = find(id) ?: return null
        val updated = existing.copy(enabled = enabled)
        save(updated)
        return updated
    }

    fun restoreDefault(definition: AlarmDefaults.Definition): Alarm? {
        val current = all()
        if (AlarmDefaults.findExisting(current, definition) != null) return null
        val restored = AlarmDefaults.create(nextId(), definition)
        save(restored)
        return restored
    }

    fun restoreMissingDefaults(): List<Alarm> = AlarmDefaults.definitions.mapNotNull(::restoreDefault)

    fun ensureDefaultAlarms(): List<Alarm> {
        val current = all().toMutableList()
        val changed = migrateCorvithPreset(current).toMutableList()
        if (!prefs.getBoolean(KEY_DEFAULTS_V1_SEEDED, false)) {
            AlarmDefaults.definitions.forEach { definition ->
                val exists = current.any {
                    it.hour == definition.hour &&
                        it.minute == definition.minute &&
                        it.label == definition.label &&
                        it.zoneId == AlarmDefaults.BRASILIA_ZONE
                }
                if (!exists) {
                    val alarm = AlarmDefaults.create(nextId(), definition)
                    current += alarm
                    changed += alarm
                }
            }
            prefs.edit().putBoolean(KEY_DEFAULTS_V1_SEEDED, true).apply()
        }
        if (changed.isNotEmpty()) write(current)
        return changed
    }

    private fun migrateCorvithPreset(current: MutableList<Alarm>): List<Alarm> {
        if (prefs.getBoolean(KEY_CORVITH_1555_MIGRATED, false)) return emptyList()
        val changed = mutableListOf<Alarm>()
        val index = current.indexOfFirst {
            it.label == "Zyrvorthian Corvith" &&
                it.hour == 16 && it.minute == 55 &&
                it.zoneId == AlarmDefaults.BRASILIA_ZONE
        }
        if (index >= 0) {
            val corrected = current[index].copy(hour = 15, minute = 55)
            current[index] = corrected
            changed += corrected
        }
        prefs.edit().putBoolean(KEY_CORVITH_1555_MIGRATED, true).apply()
        return changed
    }

    private fun write(alarms: List<Alarm>) {
        prefs.edit().putString(KEY_ALARMS, AlarmCodec.encode(alarms)).apply()
    }

    private companion object {
        const val KEY_ALARMS = "alarms_items_v1"
        const val KEY_NEXT_ID = "alarms_next_id_v1"
        const val KEY_DEFAULTS_V1_SEEDED = "alarms_defaults_zyrvorthian_v1"
        const val KEY_CORVITH_1555_MIGRATED = "alarms_corvith_1555_migrated_v1"
    }
}
