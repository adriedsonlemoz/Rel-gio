package com.example.relogioflutuante.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun FinishedCountdownDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tempo esgotado") },
        text = { Text("A contagem regressiva chegou a 00:00:00.") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
    )
}
