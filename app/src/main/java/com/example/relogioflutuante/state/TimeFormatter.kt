package com.example.relogioflutuante.state

fun formatDuration(millis: Long): String {
    val safeMillis = millis.coerceAtLeast(0L)
    val totalSeconds = if (safeMillis == 0L) 0L else (safeMillis + 999L) / 1000L
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
