package com.example.relogioflutuante.state

import android.content.Context

enum class OverlayMode { CLOCK, COUNTDOWN }

object OverlayState {
    private const val KEY_ENABLED = "overlay_enabled"
    private const val KEY_MODE = "overlay_mode"
    private const val KEY_X = "overlay_x"
    private const val KEY_Y = "overlay_y"
    private const val KEY_NOTIFICATION_ONLY = "overlay_notification_only"

    fun isEnabled(context: Context): Boolean =
        context.appPreferences().getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.appPreferences().edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun mode(context: Context): OverlayMode = runCatching {
        OverlayMode.valueOf(
            context.appPreferences().getString(KEY_MODE, OverlayMode.CLOCK.name)!!
        )
    }.getOrDefault(OverlayMode.CLOCK)

    fun setMode(context: Context, mode: OverlayMode) {
        context.appPreferences().edit().putString(KEY_MODE, mode.name).apply()
    }

    fun notificationOnly(context: Context): Boolean =
        context.appPreferences().getBoolean(KEY_NOTIFICATION_ONLY, false)

    fun setNotificationOnly(context: Context, enabled: Boolean) {
        context.appPreferences().edit().putBoolean(KEY_NOTIFICATION_ONLY, enabled).apply()
    }

    fun position(context: Context): Pair<Int, Int> =
        context.appPreferences().getInt(KEY_X, 24) to
            context.appPreferences().getInt(KEY_Y, 120)

    fun savePosition(context: Context, x: Int, y: Int) {
        context.appPreferences().edit().putInt(KEY_X, x).putInt(KEY_Y, y).apply()
    }
}
