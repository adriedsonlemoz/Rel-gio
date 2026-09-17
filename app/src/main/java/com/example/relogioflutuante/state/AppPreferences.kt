package com.example.relogioflutuante.state

import android.content.Context
import android.content.SharedPreferences

internal const val PREFS_NAME = "relogio_flutuante_state"

internal fun Context.appPreferences(): SharedPreferences =
    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
