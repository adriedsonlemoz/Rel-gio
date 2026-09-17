package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.relogioflutuante.state.OverlayMode

@Composable
fun OverlayModeSelector(
    mode: OverlayMode,
    onModeChange: (OverlayMode) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = mode == OverlayMode.CLOCK,
            onClick = { onModeChange(OverlayMode.CLOCK) },
            label = { Text("Relógio") }
        )
        FilterChip(
            selected = mode == OverlayMode.COUNTDOWN,
            onClick = { onModeChange(OverlayMode.COUNTDOWN) },
            label = { Text("Contagem") }
        )
    }
}
