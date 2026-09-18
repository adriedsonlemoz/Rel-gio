package com.example.relogioflutuante.ui.components.alarms

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.alarms.Alarm
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun AlarmOptionsSummary(alarm: Alarm) {
    val text = buildString {
        append(alarm.sound.label)
        append(if (alarm.vibrate) " • vibração" else " • sem vibração")
        if (alarm.snoozeMinutes > 0) append(" • soneca ${alarm.snoozeMinutes} min")
        else append(" • sem soneca")
    }
    Text(
        text = text,
        modifier = Modifier.padding(top = 4.dp),
        color = AppColors.TextSecondary,
        fontSize = 11.sp
    )
}
