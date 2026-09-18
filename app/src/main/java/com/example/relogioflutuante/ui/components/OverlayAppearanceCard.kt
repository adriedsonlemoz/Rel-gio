package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.state.OverlayAppearance
import com.example.relogioflutuante.state.OverlayPreset
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
    onLockedChange: (Boolean) -> Unit,
    onPresetApply: (OverlayPreset) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Aparência", color = AppColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Ajuste o overlay para cada situação", color = AppColors.TextSecondary, fontSize = 12.sp)
                }
                Surface(color = AppColors.Accent.copy(alpha = 0.13f), shape = RoundedCornerShape(10.dp)) {
                    Text(
                        "${appearance.opacityPercent}%",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        color = AppColors.AccentSoft,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            SectionLabel("Presets rápidos")
            Spacer(Modifier.height(7.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OverlayPreset.entries.forEach { preset ->
                    PresetCard(
                        modifier = Modifier.weight(1f),
                        preset = preset,
                        onClick = { onPresetApply(preset) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Surface(color = AppColors.SurfaceStrong.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(12.dp)) {
                    SectionLabel("Formato")
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        FormatChip("HH:MM:SS", OverlayTimeFormat.FULL, appearance, onFormatChange)
                        FormatChip("MM:SS", OverlayTimeFormat.MINUTES_SECONDS, appearance, onFormatChange)
                        FormatChip(":SS", OverlayTimeFormat.SECONDS_ONLY, appearance, onFormatChange)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Surface(color = AppColors.SurfaceStrong.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(12.dp)) {
                    SectionLabel("Tamanho")
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        SizeChip("Pequeno", OverlaySize.SMALL, appearance, onSizeChange)
                        SizeChip("Médio", OverlaySize.MEDIUM, appearance, onSizeChange)
                        SizeChip("Grande", OverlaySize.LARGE, appearance, onSizeChange)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Surface(color = AppColors.SurfaceStrong.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionLabel("Opacidade", Modifier.weight(1f))
                        Text(
                            "${appearance.opacityPercent}%",
                            color = AppColors.AccentSoft,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        modifier = Modifier.height(34.dp),
                        value = appearance.opacityPercent.toFloat(),
                        onValueChange = { onOpacityChange(it.roundToInt()) },
                        valueRange = 40f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = AppColors.AccentSoft,
                            activeTrackColor = AppColors.Accent,
                            inactiveTrackColor = AppColors.TextSecondary.copy(alpha = 0.14f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AppColors.SurfaceStrong.copy(alpha = 0.72f),
                shape = RoundedCornerShape(15.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Bloquear posição", color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
                        Text(
                            if (appearance.positionLocked) "Toques passam direto para o jogo." else "Permite mover e fechar a janela.",
                            color = AppColors.TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
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
}

@Composable
private fun PresetCard(modifier: Modifier, preset: OverlayPreset, onClick: () -> Unit) {
    val description = when (preset) {
        OverlayPreset.GAME -> "Equilibrado\ne bloqueado"
        OverlayPreset.DISCREET -> "Menor e\ntransparente"
        OverlayPreset.COUNTDOWN -> "Segundos em\ndestaque"
    }
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = AppColors.SurfaceStrong,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                preset.label,
                color = AppColors.AccentSoft,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                description,
                color = AppColors.TextSecondary,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier, color = AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
        shape = RoundedCornerShape(12.dp),
        label = { Text(label, fontSize = 11.sp) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = AppColors.Surface,
            labelColor = AppColors.TextSecondary,
            selectedContainerColor = AppColors.Accent.copy(alpha = 0.25f),
            selectedLabelColor = AppColors.AccentSoft
        )
    )
}
