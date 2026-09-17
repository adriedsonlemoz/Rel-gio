package com.example.relogioflutuante.state

import android.content.Context
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ClockState {
    private const val KEY_OFFSET_MS = "clock_offset_ms"
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    fun displayedLocalTime(context: Context): LocalTime {
        val offset = context.appPreferences().getLong(KEY_OFFSET_MS, 0L)
        return LocalTime.now().plusNanos(offset * 1_000_000L)
    }

    fun formattedTime(context: Context): String = displayedLocalTime(context).format(formatter)

    fun setDisplayedTime(context: Context, hour: Int, minute: Int, second: Int) {
        val now = LocalTime.now()
        val target = LocalTime.of(hour, minute, second)
        val offset = ClockOffsetCalculator.offsetMillis(now, target)
        context.appPreferences().edit().putLong(KEY_OFFSET_MS, offset).apply()
    }

    fun resetToSystemTime(context: Context) {
        context.appPreferences().edit().putLong(KEY_OFFSET_MS, 0L).apply()
    }

    fun offsetMillis(context: Context): Long =
        context.appPreferences().getLong(KEY_OFFSET_MS, 0L)
}
