package com.example.relogioflutuante.alarms

object AlarmListRules {
    fun sortedByNextTrigger(alarms: List<Alarm>, nowMillis: Long): List<Alarm> =
        alarms.sortedWith(
            compareBy<Alarm> { !it.enabled }
                .thenBy { alarm ->
                    if (alarm.enabled) {
                        AlarmTimeRules.nextTriggerMillis(alarm, nowMillis)
                    } else {
                        Long.MAX_VALUE
                    }
                }
                .thenBy { it.hour }
                .thenBy { it.minute }
                .thenBy { it.label.lowercase() }
        )
}
