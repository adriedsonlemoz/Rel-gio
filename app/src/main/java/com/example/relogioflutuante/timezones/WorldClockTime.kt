package com.example.relogioflutuante.timezones

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

data class WorldClockDisplay(
    val time: String,
    val utcOffset: String,
    val relativeOffset: String,
    val relativeDescription: String,
    val dayRelation: String?
)

object WorldClockTime {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    fun display(
        zoneId: ZoneId,
        instant: Instant,
        localZone: ZoneId
    ): WorldClockDisplay {
        val zoneTime = instant.atZone(zoneId)
        val localTime = instant.atZone(localZone)
        val zoneOffsetSeconds = zoneTime.offset.totalSeconds
        val localOffsetSeconds = localTime.offset.totalSeconds
        val differenceSeconds = zoneOffsetSeconds - localOffsetSeconds
        val dayDelta = zoneTime.toLocalDate().toEpochDay() - localTime.toLocalDate().toEpochDay()

        return WorldClockDisplay(
            time = zoneTime.format(timeFormatter),
            utcOffset = formatUtcOffset(zoneOffsetSeconds),
            relativeOffset = formatDifference(differenceSeconds),
            relativeDescription = formatRelativeDescription(differenceSeconds),
            dayRelation = when {
                dayDelta > 0 -> "amanhã"
                dayDelta < 0 -> "ontem"
                else -> null
            }
        )
    }

    fun formatUtcOffset(totalSeconds: Int): String {
        if (totalSeconds == 0) return "UTC"
        val sign = if (totalSeconds >= 0) "+" else "−"
        val totalMinutes = abs(totalSeconds) / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (minutes == 0) "UTC$sign%02d".format(hours)
        else "UTC$sign%02d:%02d".format(hours, minutes)
    }

    fun formatDifference(totalSeconds: Int): String {
        if (totalSeconds == 0) return "mesma hora"
        val sign = if (totalSeconds > 0) "+" else "−"
        val totalMinutes = abs(totalSeconds) / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            minutes == 0 -> "$sign${hours}h"
            hours == 0 -> "$sign${minutes}min"
            else -> "$sign${hours}h${minutes.toString().padStart(2, '0')}"
        }
    }

    fun formatRelativeDescription(totalSeconds: Int): String {
        if (totalSeconds == 0) return "mesma hora"
        val totalMinutes = abs(totalSeconds) / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        val amount = when {
            minutes == 0 -> "${hours}h"
            hours == 0 -> "${minutes}min"
            else -> "${hours}h ${minutes}min"
        }
        return if (totalSeconds > 0) "$amount à frente" else "$amount atrás"
    }
}
