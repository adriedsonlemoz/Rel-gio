package com.example.relogioflutuante.alarms

import java.time.DayOfWeek

data class Alarm(
    val id: Long,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val enabled: Boolean = true,
    val zoneId: String? = null,
    val sound: AlarmSound = AlarmSound.ALARM,
    val vibrate: Boolean = true,
    val snoozeMinutes: Int = 5
) {
    init {
        require(hour in 0..23)
        require(minute in 0..59)
        require(snoozeMinutes in setOf(0, 5, 10, 15))
    }

    val isRepeating: Boolean get() = repeatDays.isNotEmpty()
}
