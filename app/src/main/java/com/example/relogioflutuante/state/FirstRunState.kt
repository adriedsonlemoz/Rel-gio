package com.example.relogioflutuante.state

import android.content.Context

object FirstRunState {
    private const val KEY_SETUP_COMPLETE = "first_run_setup_complete"

    fun isSetupComplete(context: Context): Boolean =
        context.appPreferences().getBoolean(KEY_SETUP_COMPLETE, false)

    fun setSetupComplete(context: Context, complete: Boolean) {
        context.appPreferences().edit().putBoolean(KEY_SETUP_COMPLETE, complete).apply()
    }
}
