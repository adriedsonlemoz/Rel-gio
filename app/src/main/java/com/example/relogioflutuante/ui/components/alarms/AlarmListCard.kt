package com.example.relogioflutuante.ui.components.alarms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.R
import com.example.relogioflutuante.alarms.Alarm
import com.example.relogioflutuante.alarms.AlarmDefaults
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
    val nextAlarm = alarms.firstOrNull { it.enabled }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Surface,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "ALARMES",
                        color = AppColors.AccentSoft,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        if (alarms.isEmpty()) "Organize seus próximos avisos" else "${alarms.count { it.enabled }} ativos de ${alarms.size}",
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    nextAlarm?.let {
                        Text(
                            "Próximo: ${it.label.ifBlank { "Alarme" }} • ${AlarmFormatting.nextTriggerSummary(it, nowMillis)}",
                            color = AppColors.AccentSoft,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                if (alarms.isNotEmpty()) {
                    Surface(color = AppColors.Accent.copy(alpha = 0.13f), shape = CircleShape) {
                        Text(
                            alarms.size.toString(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            color = AppColors.AccentSoft,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            if (alarms.isEmpty()) {
                EmptyAlarmState()
            } else {
                alarms.forEachIndexed { index, alarm ->
                    AlarmRow(alarm, nowMillis, onToggle, onEdit, onDelete)
                    if (index != alarms.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 15.dp),
                            color = AppColors.TextSecondary.copy(alpha = 0.08f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyAlarmState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(color = AppColors.Accent.copy(alpha = 0.12f), shape = CircleShape) {
            Icon(
                painter = painterResource(R.drawable.ic_nav_alarm),
                contentDescription = null,
                tint = AppColors.AccentSoft,
                modifier = Modifier.padding(14.dp).size(26.dp)
            )
        }
        Text(
            "Nenhum alarme ainda",
            color = AppColors.TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            "Crie um alarme único ou escolha os dias da semana para repetir.",
            color = AppColors.TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 5.dp)
        )
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
        modifier = Modifier.fillMaxWidth().clickable { onEdit(alarm) }.padding(horizontal = 15.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    AlarmFormatting.time(alarm),
                    color = if (alarm.enabled) AppColors.TextPrimary else AppColors.TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (alarm.zoneId == AlarmDefaults.BRASILIA_ZONE) ZoneBadge()
            }
            Text(
                alarm.label.ifBlank { "Alarme" },
                color = if (alarm.enabled) AppColors.TextPrimary else AppColors.TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            if (alarm.repeatDays.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    DayOfWeek.entries.filter { it in alarm.repeatDays }.forEach { day ->
                        RepeatDayChip(AlarmFormatting.shortDay(day), alarm.enabled)
                    }
                }
            } else {
                Text("Uma vez", color = AppColors.TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
            }
            Text(
                AlarmFormatting.nextTriggerSummary(alarm, nowMillis),
                modifier = Modifier.padding(top = 5.dp),
                color = if (alarm.enabled) AppColors.AccentSoft else AppColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (alarm.enabled) FontWeight.SemiBold else FontWeight.Normal
            )
            AlarmOptionsSummary(alarm)
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
                DropdownMenuItem(text = { Text("Editar") }, onClick = { menuOpen = false; onEdit(alarm) })
                DropdownMenuItem(text = { Text("Excluir") }, onClick = { menuOpen = false; onDelete(alarm) })
            }
        }
    }
}
