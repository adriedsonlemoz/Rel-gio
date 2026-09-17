package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.state.OverlayAppearance
import com.example.relogioflutuante.state.OverlaySize
import com.example.relogioflutuante.state.OverlayTimeFormat
import com.example.relogioflutuante.ui.theme.AppColors
import kotlin.math.roundToInt

@Composable
fun OverlayAppearanceCard(
    appearance: OverlayAppearance,
    onFormatChange: (OverlayTimeFormat) -> Unit,
    onSizeChange: (OverlaySize) -> Unit,
    onOpacityChange: (Int) -> Unit,
    onLockedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Aparência da janela", color = AppColors.TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("Formato", color = AppColors.TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(5.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                FormatChip("HH:MM:SS", OverlayTimeFormat.FULL, appearance, onFormatChange)
                FormatChip("MM:SS", OverlayTimeFormat.MINUTES_SECONDS, appearance, onFormatChange)
                FormatChip(":SS", OverlayTimeFormat.SECONDS_ONLY, appearance, onFormatChange)
            }
            Spacer(Modifier.height(12.dp))
            Text("Tamanho", color = AppColors.TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(5.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                SizeChip("Pequeno", OverlaySize.SMALL, appearance, onSizeChange)
                SizeChip("Médio", OverlaySize.MEDIUM, appearance, onSizeChange)
                SizeChip("Grande", OverlaySize.LARGE, appearance, onSizeChange)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Opacidade", color = AppColors.TextPrimary, modifier = Modifier.weight(1f))
                Text("${appearance.opacityPercent}%", color = AppColors.TextSecondary)
            }
            Slider(
                value = appearance.opacityPercent.toFloat(),
                onValueChange = { onOpacityChange(it.roundToInt()) },
                valueRange = 40f..100f,
                steps = 5
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Bloquear posição", color = AppColors.TextPrimary)
                    Text(
                        "Quando bloqueado, a janela não captura nenhum toque do jogo.",
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
                Switch(checked = appearance.positionLocked, onCheckedChange = onLockedChange)
            }
        }
    }
}

@Composable
private fun FormatChip(
    label: String,
    value: OverlayTimeFormat,
    appearance: OverlayAppearance,
    onChange: (OverlayTimeFormat) -> Unit
) {
    FilterChip(
        selected = appearance.timeFormat == value,
        onClick = { onChange(value) },
        label = { Text(label) }
    )
}

@Composable
private fun SizeChip(
    label: String,
    value: OverlaySize,
    appearance: OverlayAppearance,
    onChange: (OverlaySize) -> Unit
) {
    FilterChip(
        selected = appearance.size == value,
        onClick = { onChange(value) },
        label = { Text(label) }
    )
}
