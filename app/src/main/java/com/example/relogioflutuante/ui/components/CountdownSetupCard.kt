package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun CountdownSetupCard(
    hours: Int,
    minutes: Int,
    seconds: Int,
    enabled: Boolean,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit,
    onSecondsChange: (Int) -> Unit,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text("Definir tempo", color = AppColors.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    "Ajuste sem abrir o teclado",
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DurationStepper("Horas", hours, 99, Modifier.weight(1f), onHoursChange)
                DurationStepper("Min", minutes, 59, Modifier.weight(1f), onMinutesChange)
                DurationStepper("Seg", seconds, 59, Modifier.weight(1f), onSecondsChange)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                QuickTimeChip("30 s", Modifier.weight(1f)) {
                    onHoursChange(0); onMinutesChange(0); onSecondsChange(30)
                }
                QuickTimeChip("1 min", Modifier.weight(1f)) {
                    onHoursChange(0); onMinutesChange(1); onSecondsChange(0)
                }
                QuickTimeChip("5 min", Modifier.weight(1f)) {
                    onHoursChange(0); onMinutesChange(5); onSecondsChange(0)
                }
                QuickTimeChip("10 min", Modifier.weight(1f)) {
                    onHoursChange(0); onMinutesChange(10); onSecondsChange(0)
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled && (hours > 0 || minutes > 0 || seconds > 0),
                onClick = onApply,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
            ) {
                Text("Aplicar tempo")
            }
        }
    }
}

@Composable
private fun DurationStepper(
    label: String,
    value: Int,
    max: Int,
    modifier: Modifier,
    onChange: (Int) -> Unit
) {
    Surface(
        modifier = modifier,
        color = AppColors.SurfaceStrong,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = AppColors.TextSecondary, fontSize = 11.sp)
            Text(
                value.toString().padStart(2, '0'),
                color = AppColors.TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepControl("−") { onChange(if (value <= 0) max else value - 1) }
                StepControl("+") { onChange(if (value >= max) 0 else value + 1) }
            }
        }
    }
}

@Composable
private fun StepControl(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .size(34.dp)
            .clickable(onClick = onClick),
        color = AppColors.Accent.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp)
    ) {
        androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
            Text(label, color = AppColors.AccentSoft, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun QuickTimeChip(label: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = AppColors.SurfaceStrong,
        shape = RoundedCornerShape(11.dp)
    ) {
        Text(
            label,
            modifier = Modifier.padding(vertical = 8.dp),
            color = AppColors.AccentSoft,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}
