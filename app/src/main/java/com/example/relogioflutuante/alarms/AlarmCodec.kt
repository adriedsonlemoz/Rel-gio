package com.example.relogioflutuante.alarms

import java.nio.charset.StandardCharsets
import java.time.DayOfWeek
import java.util.Base64

object AlarmCodec {
    fun encode(alarms: List<Alarm>): String = alarms.joinToString(";") { alarm ->
        val label = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(alarm.label.toByteArray(StandardCharsets.UTF_8))
        val daysMask = alarm.repeatDays.fold(0) { mask, day ->
            mask or (1 shl (day.value - 1))
        }
        listOf(
            alarm.id,
            alarm.hour,
            alarm.minute,
            if (alarm.enabled) 1 else 0,
            daysMask,
            label
        ).joinToString(":")
    }

    fun decode(raw: String?): List<Alarm> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(';').mapNotNull(::decodeAlarm).sortedWith(
            compareBy<Alarm> { it.hour }.thenBy { it.minute }.thenBy { it.id }
        )
    }

    private fun decodeAlarm(raw: String): Alarm? = runCatching {
        val fields = raw.split(':', limit = 6)
        if (fields.size != 6) return null
        val mask = fields[4].toInt()
        Alarm(
            id = fields[0].toLong(),
            hour = fields[1].toInt(),
            minute = fields[2].toInt(),
            enabled = fields[3] == "1",
            repeatDays = DayOfWeek.entries.filterTo(mutableSetOf()) { day ->
                mask and (1 shl (day.value - 1)) != 0
            },
            label = String(
                Base64.getUrlDecoder().decode(fields[5]),
                StandardCharsets.UTF_8
            )
        )
    }.getOrNull()
}
