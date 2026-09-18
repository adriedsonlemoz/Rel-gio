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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
            Spacer(Modifier.height(13.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Opacidade", color = AppColors.TextPrimary, modifier = Modifier.weight(1f))
                Text("${appearance.opacityPercent}%", color = AppColors.AccentSoft, fontWeight = FontWeight.SemiBold)
            }
            Slider(
                value = appearance.opacityPercent.toFloat(),
                onValueChange = { onOpacityChange(it.roundToInt()) },
                valueRange = 40f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = AppColors.AccentSoft,
                    activeTrackColor = AppColors.Accent,
                    inactiveTrackColor = AppColors.SurfaceStrong,
                    activeTickColor = AppColors.Accent,
                    inactiveTickColor = AppColors.SurfaceStrong
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Bloquear posição", color = AppColors.TextPrimary)
                    Text(
                        "Bloqueado, o relógio não intercepta toques do jogo.",
                        color = AppColors.TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
                Switch(
                    checked = appearance.positionLocked,
                    onCheckedChange = onLockedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppColors.TextPrimary,
                        checkedTrackColor = AppColors.Accent,
                        uncheckedTrackColor = AppColors.SurfaceStrong
                    )
                )
            }
        }
    }
}

@Composable
private fun FormatChip(label: String, value: OverlayTimeFormat, appearance: OverlayAppearance, onChange: (OverlayTimeFormat) -> Unit) {
    StyledChip(label, appearance.timeFormat == value) { onChange(value) }
}

@Composable
private fun SizeChip(label: String, value: OverlaySize, appearance: OverlayAppearance, onChange: (OverlaySize) -> Unit) {
    StyledChip(label, appearance.size == value) { onChange(value) }
}

@Composable
private fun StyledChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = AppColors.SurfaceStrong,
            labelColor = AppColors.TextSecondary,
            selectedContainerColor = AppColors.Accent.copy(alpha = 0.28f),
            selectedLabelColor = AppColors.AccentSoft
        )
    )
}
