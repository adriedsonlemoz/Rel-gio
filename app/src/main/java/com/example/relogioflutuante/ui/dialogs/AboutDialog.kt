package com.example.relogioflutuante.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.relogioflutuante.state.appVersionName

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val version = appVersionName(context)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sobre") },
        text = {
            Text(
                "Relógio Flutuante\n\n" +
                    "Versão $version\n\n" +
                    "Mostra hora ou contagem regressiva sobre outros aplicativos. " +
                    "O modo por Acessibilidade apenas desenha a janela; não lê a tela e não executa cliques ou gestos."
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        }
    )
}
