package com.example.relogioflutuante.state

import android.content.Context

object SetupGuideState {
    private const val KEY_RESTRICTED_SETTINGS_CONFIRMED = "restricted_settings_confirmed"

    fun isRestrictedSettingsConfirmed(context: Context): Boolean =
        context.appPreferences().getBoolean(KEY_RESTRICTED_SETTINGS_CONFIRMED, false)

    fun setRestrictedSettingsConfirmed(context: Context, confirmed: Boolean) {
        context.appPreferences().edit()
            .putBoolean(KEY_RESTRICTED_SETTINGS_CONFIRMED, confirmed)
            .apply()
    }
}
