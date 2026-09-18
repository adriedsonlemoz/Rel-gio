package com.example.relogioflutuante.alarms

import java.time.DayOfWeek

object AlarmDefaults {
    const val BRASILIA_ZONE = "America/Sao_Paulo"
    private val EVERY_DAY = DayOfWeek.entries.toSet()

    data class Definition(val hour: Int, val minute: Int, val label: String)

    val definitions = listOf(
        Definition(15, 55, "Zyrvorthian Corvith"),
        Definition(18, 55, "Zyrvorthian Ortson"),
        Definition(19, 55, "Zyrvorthian Zulanka")
    )

    fun create(id: Long, definition: Definition): Alarm = Alarm(
        id = id,
        hour = definition.hour,
        minute = definition.minute,
        label = definition.label,
        repeatDays = EVERY_DAY,
        enabled = true,
        zoneId = BRASILIA_ZONE
    )
}
