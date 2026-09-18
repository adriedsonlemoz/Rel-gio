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
fun CountdownActions(
    isRunning: Boolean,
    isPaused: Boolean,
    canStart: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStart: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when {
            isRunning -> Button(
                modifier = Modifier.weight(1f),
                onClick = onPause
            ) { Text("Pausar") }
            isPaused -> Button(
                modifier = Modifier.weight(1f),
                onClick = onResume
            ) { Text("Continuar") }
            else -> Button(
                modifier = Modifier.weight(1f),
                enabled = canStart,
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
            ) { Text("Iniciar") }
        }
        OutlinedButton(
            modifier = Modifier.weight(1f),
            enabled = canStart,
            onClick = onReset
        ) { Text("Zerar") }
    }
}
