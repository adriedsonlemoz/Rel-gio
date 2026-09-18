package com.example.relogioflutuante.ui.dialogs.alarms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.alarms.Alarm
import com.example.relogioflutuante.alarms.AlarmFormatting
import com.example.relogioflutuante.alarms.AlarmSound
import com.example.relogioflutuante.ui.theme.AppColors
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun AlarmEditorDialog(
    alarm: Alarm?,
    onDismiss: () -> Unit,
    onConfirm: (
        hour: Int,
        minute: Int,
        label: String,
        days: Set<DayOfWeek>,
        sound: AlarmSound,
        vibrate: Boolean,
        snoozeMinutes: Int
    ) -> Unit
) {
    val defaultTime = remember { LocalTime.now().plusMinutes(5) }
    var hour by remember(alarm?.id) { mutableIntStateOf(alarm?.hour ?: defaultTime.hour) }
    var minute by remember(alarm?.id) { mutableIntStateOf(alarm?.minute ?: defaultTime.minute) }
    var label by remember(alarm?.id) { mutableStateOf(alarm?.label.orEmpty()) }
    var days by remember(alarm?.id) { mutableStateOf(alarm?.repeatDays.orEmpty()) }
    var sound by remember(alarm?.id) { mutableStateOf(alarm?.sound ?: AlarmSound.ALARM) }
    var vibrate by remember(alarm?.id) { mutableStateOf(alarm?.vibrate ?: true) }
    var snoozeMinutes by remember(alarm?.id) { mutableIntStateOf(alarm?.snoozeMinutes ?: 5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(if (alarm == null) "Novo alarme" else "Editar alarme", fontWeight = FontWeight.Bold)
                Text(
                    "Horário, repetição e aviso",
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 570.dp).verticalScroll(rememberScrollState())
            ) {
                EditorSection("HORÁRIO") {
                    AlarmTimeSelector(
                        hour = hour,
                        minute = minute,
                        onHourChange = { hour = it },
                        onMinuteChange = { minute = it }
                    )
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it.take(40) },
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        label = { Text("Nome opcional") },
                        singleLine = true
                    )
                }

                EditorSection("REPETIÇÃO", Modifier.padding(top = 10.dp)) {
                    RepeatDaySelector(days) { day ->
                        days = if (day in days) days - day else days + day
                    }
                    Text(
                        AlarmFormatting.repeatSummary(days),
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                EditorSection("AVISO", Modifier.padding(top = 10.dp)) {
                    AlarmOptionsSelector(
                        sound = sound,
                        vibrate = vibrate,
                        snoozeMinutes = snoozeMinutes,
                        onSoundChange = { sound = it },
                        onVibrateChange = { vibrate = it },
                        onSnoozeChange = { snoozeMinutes = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(hour, minute, label.trim(), days, sound, vibrate, snoozeMinutes)
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EditorSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppColors.Surface.copy(alpha = 0.72f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                title,
                color = AppColors.AccentSoft,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 9.dp)
            )
            content()
        }
    }
}
