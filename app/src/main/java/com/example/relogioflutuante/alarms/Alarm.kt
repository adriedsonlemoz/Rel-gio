package com.example.relogioflutuante.alarms

import java.time.DayOfWeek

data class Alarm(
    val id: Long,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val enabled: Boolean = true
) {
    init {
        require(hour in 0..23)
        require(minute in 0..59)
    }

    val isRepeating: Boolean get() = repeatDays.isNotEmpty()
}
