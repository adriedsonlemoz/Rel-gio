package com.example.relogioflutuante.ui.dialogs.alarms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.alarms.AlarmFormatting
import com.example.relogioflutuante.ui.theme.AppColors
import java.time.DayOfWeek

@Composable
fun AlarmTimeSelector(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TimeUnitSelector("Hora", hour, 23, Modifier.weight(1f), onHourChange)
        TimeUnitSelector("Minuto", minute, 59, Modifier.weight(1f), onMinuteChange)
    }
}

@Composable
private fun TimeUnitSelector(
    label: String,
    value: Int,
    max: Int,
    modifier: Modifier,
    onChange: (Int) -> Unit
) {
    Surface(modifier = modifier, color = AppColors.Surface, shape = RoundedCornerShape(16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = AppColors.TextSecondary, fontSize = 11.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepButton("−") { onChange(if (value <= 0) max else value - 1) }
                Text(
                    text = value.toString().padStart(2, '0'),
                    modifier = Modifier.weight(1f),
                    color = AppColors.TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                StepButton("+") { onChange(if (value >= max) 0 else value + 1) }
            }
        }
    }
}

@Composable
private fun StepButton(text: String, onClick: () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
        Text(text, color = AppColors.AccentSoft, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RepeatDaySelector(
    selectedDays: Set<DayOfWeek>,
    onToggle: (DayOfWeek) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DayOfWeek.entries.forEach { day ->
            val selected = day in selectedDays
            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .clickable { onToggle(day) },
                color = if (selected) AppColors.Accent else AppColors.Surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                    Text(
                        AlarmFormatting.compactDay(day),
                        color = if (selected) AppColors.TextPrimary else AppColors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
