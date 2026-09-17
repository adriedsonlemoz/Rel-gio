package com.example.relogioflutuante.state

import android.content.Context
import android.content.res.Configuration

object OverlayPositionState {
    private const val KEY_PORTRAIT_X = "overlay_portrait_x"
    private const val KEY_PORTRAIT_Y = "overlay_portrait_y"
    private const val KEY_LANDSCAPE_X = "overlay_landscape_x"
    private const val KEY_LANDSCAPE_Y = "overlay_landscape_y"
    private const val LEGACY_X = "overlay_x"
    private const val LEGACY_Y = "overlay_y"

    fun position(context: Context): Pair<Int, Int> {
        val prefs = context.appPreferences()
        val landscape = isLandscape(context)
        val xKey = if (landscape) KEY_LANDSCAPE_X else KEY_PORTRAIT_X
        val yKey = if (landscape) KEY_LANDSCAPE_Y else KEY_PORTRAIT_Y
        val defaultY = if (landscape) 48 else 120
        val fallbackX = prefs.getInt(LEGACY_X, 24)
        val fallbackY = prefs.getInt(LEGACY_Y, defaultY)
        return prefs.getInt(xKey, fallbackX) to prefs.getInt(yKey, fallbackY)
    }

    fun savePosition(context: Context, x: Int, y: Int) {
        val landscape = isLandscape(context)
        val xKey = if (landscape) KEY_LANDSCAPE_X else KEY_PORTRAIT_X
        val yKey = if (landscape) KEY_LANDSCAPE_Y else KEY_PORTRAIT_Y
        context.appPreferences().edit()
            .putInt(xKey, x.coerceAtLeast(0))
            .putInt(yKey, y.coerceAtLeast(0))
            .apply()
    }

    fun orientation(context: Context): Int = context.resources.configuration.orientation

    private fun isLandscape(context: Context): Boolean =
        orientation(context) == Configuration.ORIENTATION_LANDSCAPE
}
