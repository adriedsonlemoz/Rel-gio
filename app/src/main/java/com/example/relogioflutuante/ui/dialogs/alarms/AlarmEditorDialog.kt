package com.example.relogioflutuante.ui.dialogs.alarms

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.relogioflutuante.alarms.Alarm
import com.example.relogioflutuante.alarms.AlarmFormatting
import com.example.relogioflutuante.ui.components.DurationField
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun AlarmEditorDialog(
    alarm: Alarm?,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int, label: String, days: Set<DayOfWeek>) -> Unit
) {
    val defaultTime = remember { LocalTime.now().plusMinutes(5) }
    var hour by remember(alarm?.id) {
        mutableStateOf((alarm?.hour ?: defaultTime.hour).toString().padStart(2, '0'))
    }
    var minute by remember(alarm?.id) {
        mutableStateOf((alarm?.minute ?: defaultTime.minute).toString().padStart(2, '0'))
    }
    var label by remember(alarm?.id) { mutableStateOf(alarm?.label.orEmpty()) }
    var days by remember(alarm?.id) { mutableStateOf(alarm?.repeatDays.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (alarm == null) "Novo alarme" else "Editar alarme") },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DurationField("Hora", hour, 23, Modifier.weight(1f)) { hour = it }
                    DurationField("Min", minute, 59, Modifier.weight(1f)) { minute = it }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it.take(40) },
                    label = { Text("Nome opcional") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Text("Repetir")
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DayOfWeek.entries.forEach { day ->
                        FilterChip(
                            selected = day in days,
                            onClick = {
                                days = if (day in days) days - day else days + day
                            },
                            label = { Text(AlarmFormatting.shortDay(day)) }
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    AlarmFormatting.repeatSummary(days),
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        (hour.toIntOrNull() ?: 0).coerceIn(0, 23),
                        (minute.toIntOrNull() ?: 0).coerceIn(0, 59),
                        label.trim(),
                        days
                    )
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
