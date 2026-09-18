package com.example.relogioflutuante.ui.components.alarms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.R
import com.example.relogioflutuante.alarms.Alarm
import com.example.relogioflutuante.alarms.AlarmFormatting
import com.example.relogioflutuante.ui.theme.AppColors
import java.time.DayOfWeek

@Composable
fun AlarmListCard(
    alarms: List<Alarm>,
    nowMillis: Long,
    onToggle: (Alarm, Boolean) -> Unit,
    onEdit: (Alarm) -> Unit,
    onDelete: (Alarm) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Surface,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "ALARMES",
                modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 14.dp, bottom = 8.dp),
                color = AppColors.AccentSoft,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            if (alarms.isEmpty()) {
                Text(
                    "Nenhum alarme criado.",
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 15.dp),
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp
                )
            } else {
                alarms.forEachIndexed { index, alarm ->
                    AlarmRow(alarm, nowMillis, onToggle, onEdit, onDelete)
                    if (index != alarms.lastIndex) {
                        HorizontalDivider(color = AppColors.TextSecondary.copy(alpha = 0.08f))
                    }
                }
            }
        }
    }
}

@Composable
private fun AlarmRow(
    alarm: Alarm,
    nowMillis: Long,
    onToggle: (Alarm, Boolean) -> Unit,
    onEdit: (Alarm) -> Unit,
    onDelete: (Alarm) -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(alarm) }
            .padding(horizontal = 15.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                AlarmFormatting.time(alarm),
                color = if (alarm.enabled) AppColors.TextPrimary else AppColors.TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                alarm.label.ifBlank { "Alarme" },
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            if (alarm.repeatDays.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DayOfWeek.entries.filter { it in alarm.repeatDays }.forEach { day ->
                        RepeatDayChip(AlarmFormatting.shortDay(day))
                    }
                }
            } else {
                Text("Uma vez", color = AppColors.TextSecondary, fontSize = 11.sp)
            }
            Text(
                AlarmFormatting.nextTriggerSummary(alarm, nowMillis),
                modifier = Modifier.padding(top = 5.dp),
                color = if (alarm.enabled) AppColors.AccentSoft else AppColors.TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (alarm.enabled) FontWeight.SemiBold else FontWeight.Normal
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Switch(
                checked = alarm.enabled,
                onCheckedChange = { onToggle(alarm, it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppColors.TextPrimary,
                    checkedTrackColor = AppColors.Accent,
                    uncheckedTrackColor = AppColors.SurfaceStrong
                )
            )
            IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(34.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_more_vert),
                    contentDescription = "Opções do alarme",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("Editar") },
                    onClick = { menuOpen = false; onEdit(alarm) }
                )
                DropdownMenuItem(
                    text = { Text("Excluir") },
                    onClick = { menuOpen = false; onDelete(alarm) }
                )
            }
        }
    }
}

@Composable
private fun RepeatDayChip(label: String) {
    Surface(color = AppColors.Accent.copy(alpha = 0.14f), shape = RoundedCornerShape(8.dp)) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            color = AppColors.AccentSoft,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
