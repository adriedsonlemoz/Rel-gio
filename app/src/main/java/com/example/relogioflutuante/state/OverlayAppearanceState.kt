package com.example.relogioflutuante.state

import android.content.Context

object OverlayAppearanceState {
    private const val KEY_TIME_FORMAT = "overlay_time_format"
    private const val KEY_SIZE = "overlay_size"
    private const val KEY_OPACITY = "overlay_opacity"
    private const val KEY_LOCKED = "overlay_position_locked"

    fun read(context: Context): OverlayAppearance {
        val prefs = context.appPreferences()
        return OverlayAppearance(
            timeFormat = enumValueOrDefault(
                prefs.getString(KEY_TIME_FORMAT, null),
                OverlayTimeFormat.FULL
            ),
            size = enumValueOrDefault(
                prefs.getString(KEY_SIZE, null),
                OverlaySize.MEDIUM
            ),
            opacityPercent = prefs.getInt(KEY_OPACITY, 82).coerceIn(40, 100),
            positionLocked = prefs.getBoolean(KEY_LOCKED, false)
        )
    }

    fun setTimeFormat(context: Context, value: OverlayTimeFormat) {
        context.appPreferences().edit().putString(KEY_TIME_FORMAT, value.name).apply()
    }

    fun setSize(context: Context, value: OverlaySize) {
        context.appPreferences().edit().putString(KEY_SIZE, value.name).apply()
    }

    fun setOpacity(context: Context, value: Int) {
        context.appPreferences().edit().putInt(KEY_OPACITY, value.coerceIn(40, 100)).apply()
    }

    fun setPositionLocked(context: Context, locked: Boolean) {
        context.appPreferences().edit().putBoolean(KEY_LOCKED, locked).apply()
    }

    private inline fun <reified T : Enum<T>> enumValueOrDefault(
        stored: String?,
        fallback: T
    ): T = stored?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: fallback
}
