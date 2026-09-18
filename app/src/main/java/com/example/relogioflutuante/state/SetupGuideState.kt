package com.example.relogioflutuante.state

import android.content.Context

object SetupGuideState {
    private const val KEY_RESTRICTED_SETTINGS_CONFIRMED = "restricted_settings_confirmed"
    private const val KEY_RESTRICTED_SETTINGS_OPENED = "restricted_settings_opened"

    fun isRestrictedSettingsConfirmed(context: Context): Boolean =
        context.appPreferences().getBoolean(KEY_RESTRICTED_SETTINGS_CONFIRMED, false)

    fun setRestrictedSettingsConfirmed(context: Context, confirmed: Boolean) {
        context.appPreferences().edit()
            .putBoolean(KEY_RESTRICTED_SETTINGS_CONFIRMED, confirmed)
            .apply()
    }

    fun isRestrictedSettingsOpened(context: Context): Boolean =
        context.appPreferences().getBoolean(KEY_RESTRICTED_SETTINGS_OPENED, false)

    fun setRestrictedSettingsOpened(context: Context, opened: Boolean) {
        context.appPreferences().edit()
            .putBoolean(KEY_RESTRICTED_SETTINGS_OPENED, opened)
            .apply()
    }
}
