package com.example.relogioflutuante.ui.components.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.alarms.Alarm
import com.example.relogioflutuante.alarms.AlarmFormatting
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun AlarmListCard(
    alarms: List<Alarm>,
    onToggle: (Alarm, Boolean) -> Unit,
    onEdit: (Alarm) -> Unit,
    onDelete: (Alarm) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "ALARMES",
                modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 14.dp, bottom = 8.dp),
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            if (alarms.isEmpty()) {
                Text(
                    "Nenhum alarme criado.",
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 15.dp),
                    color = AppColors.TextSecondary,
                    fontSize = 14.sp
                )
            } else {
                alarms.forEachIndexed { index, alarm ->
                    AlarmRow(alarm, onToggle, onEdit, onDelete)
                    if (index != alarms.lastIndex) {
                        HorizontalDivider(color = AppColors.SurfaceStrong)
                    }
                }
            }
        }
    }
}

@Composable
private fun AlarmRow(
    alarm: Alarm,
    onToggle: (Alarm, Boolean) -> Unit,
    onEdit: (Alarm) -> Unit,
    onDelete: (Alarm) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                AlarmFormatting.time(alarm),
                color = if (alarm.enabled) AppColors.TextPrimary else AppColors.TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 27.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                alarm.label.ifBlank { "Alarme" },
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                maxLines = 1
            )
            Text(
                AlarmFormatting.repeatSummary(alarm.repeatDays),
                color = AppColors.TextSecondary,
                fontSize = 12.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { onEdit(alarm) }) { Text("Editar") }
                TextButton(onClick = { onDelete(alarm) }) { Text("Excluir") }
            }
        }
        Switch(
            checked = alarm.enabled,
            onCheckedChange = { onToggle(alarm, it) }
        )
    }
}
