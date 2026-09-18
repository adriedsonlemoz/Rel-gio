package com.example.relogioflutuante.alarms

/** Stable sound choices stored with each alarm. */
enum class AlarmSound(
    val storageKey: String,
    val label: String
) {
    ALARM("alarm", "Alarme padrão"),
    RINGTONE("ringtone", "Toque do telefone"),
    NOTIFICATION("notification", "Som de notificação"),
    SILENT("silent", "Sem som");

    companion object {
        fun fromStorage(value: String?): AlarmSound = entries.firstOrNull {
            it.storageKey == value
        } ?: ALARM
    }
}
