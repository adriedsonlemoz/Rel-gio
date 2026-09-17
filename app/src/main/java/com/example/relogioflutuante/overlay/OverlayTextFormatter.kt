package com.example.relogioflutuante.overlay

import com.example.relogioflutuante.state.OverlayTimeFormat
import java.time.LocalTime

object OverlayTextFormatter {
    fun clock(time: LocalTime, format: OverlayTimeFormat): String = when (format) {
        OverlayTimeFormat.FULL -> "%02d:%02d:%02d".format(time.hour, time.minute, time.second)
        OverlayTimeFormat.MINUTES_SECONDS -> "%02d:%02d".format(time.minute, time.second)
        OverlayTimeFormat.SECONDS_ONLY -> ":%02d".format(time.second)
    }

    fun countdown(remainingMillis: Long, format: OverlayTimeFormat): String {
        val totalSeconds = (remainingMillis.coerceAtLeast(0L) + 999L) / 1000L
        val hours = totalSeconds / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        val seconds = totalSeconds % 60L
        return when (format) {
            OverlayTimeFormat.FULL -> "%02d:%02d:%02d".format(hours, minutes, seconds)
            OverlayTimeFormat.MINUTES_SECONDS -> "%02d:%02d".format(totalSeconds / 60L, seconds)
            OverlayTimeFormat.SECONDS_ONLY -> ":%02d".format(seconds)
        }
    }
}
