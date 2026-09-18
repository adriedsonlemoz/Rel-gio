package com.example.relogioflutuante.ui.dialogs.alarms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.alarms.Alarm
import com.example.relogioflutuante.alarms.AlarmFormatting
import com.example.relogioflutuante.ui.theme.AppColors
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun AlarmEditorDialog(
    alarm: Alarm?,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int, label: String, days: Set<DayOfWeek>) -> Unit
) {
    val defaultTime = remember { LocalTime.now().plusMinutes(5) }
    var hour by remember(alarm?.id) { mutableIntStateOf(alarm?.hour ?: defaultTime.hour) }
    var minute by remember(alarm?.id) { mutableIntStateOf(alarm?.minute ?: defaultTime.minute) }
    var label by remember(alarm?.id) { mutableStateOf(alarm?.label.orEmpty()) }
    var days by remember(alarm?.id) { mutableStateOf(alarm?.repeatDays.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (alarm == null) "Novo alarme" else "Editar alarme") },
        text = {
            Column {
                AlarmTimeSelector(
                    hour = hour,
                    minute = minute,
                    onHourChange = { hour = it },
                    onMinuteChange = { minute = it }
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it.take(40) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nome opcional") },
                    singleLine = true
                )
                Spacer(Modifier.height(14.dp))
                Text("Repetir", color = AppColors.TextPrimary, fontSize = 13.sp)
                Spacer(Modifier.height(7.dp))
                RepeatDaySelector(days) { day ->
                    days = if (day in days) days - day else days + day
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    AlarmFormatting.repeatSummary(days),
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(hour, minute, label.trim(), days) }) {
                Text("Salvar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
