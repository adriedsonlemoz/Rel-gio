package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun ClockTimeActions(
    onAdjust: () -> Unit,
    onUseSystem: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onAdjust,
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
        ) { Text("Ajustar horário") }
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onUseSystem
        ) { Text("Usar sistema") }
    }
}
