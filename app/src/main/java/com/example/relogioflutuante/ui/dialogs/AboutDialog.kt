package com.example.relogioflutuante.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.state.appVersionName
import com.example.relogioflutuante.ui.components.DONATION_PIX_KEY
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
    onCopyPix: () -> Unit
) {
    val context = LocalContext.current
    val version = appVersionName(context)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sobre") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Relógio Flutuante",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Versão $version",
                    color = AppColors.AccentSoft,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Mostra hora ou contagem regressiva sobre outros aplicativos. " +
                        "O modo por Acessibilidade apenas desenha a janela; não lê a tela e não executa cliques ou gestos.",
                    lineHeight = 20.sp
                )
                Text(
                    text = "PIX para apoiar: $DONATION_PIX_KEY",
                    color = AppColors.TextPrimary,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        },
        dismissButton = {
            TextButton(onClick = onCopyPix) { Text("Copiar PIX") }
        }
    )
}
