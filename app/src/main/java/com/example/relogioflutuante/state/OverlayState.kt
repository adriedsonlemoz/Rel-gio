package com.example.relogioflutuante.state

import android.content.Context

object OverlayState {
    private const val KEY_ENABLED = "overlay_enabled"
    private const val KEY_MODE = "overlay_mode"
    private const val KEY_PRESENTATION = "overlay_presentation"
    private const val LEGACY_KEY_NOTIFICATION_ONLY = "overlay_notification_only"

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

    fun presentation(context: Context): OverlayPresentation {
        val prefs = context.appPreferences()
        val stored = prefs.getString(KEY_PRESENTATION, null)
        if (stored != null) {
            return runCatching { OverlayPresentation.valueOf(stored) }
                .getOrDefault(OverlayPresentation.SYSTEM_OVERLAY)
        }
        return if (prefs.getBoolean(LEGACY_KEY_NOTIFICATION_ONLY, false)) {
            OverlayPresentation.NOTIFICATION
        } else {
            OverlayPresentation.SYSTEM_OVERLAY
        }
    }

    fun setPresentation(context: Context, presentation: OverlayPresentation) {
        context.appPreferences().edit()
            .putString(KEY_PRESENTATION, presentation.name)
            .putBoolean(
                LEGACY_KEY_NOTIFICATION_ONLY,
                presentation == OverlayPresentation.NOTIFICATION
            )
            .apply()
    }

    fun notificationOnly(context: Context): Boolean =
        presentation(context) == OverlayPresentation.NOTIFICATION

}
