package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.overlay.OverlayCapability
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun OverlayMethodOptionsCard(
    capability: OverlayCapability,
    notificationsAllowed: Boolean,
    onAccessibility: () -> Unit,
    onSystemOverlay: () -> Unit,
    onNotification: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Outros métodos", color = AppColors.TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(
                "Use somente se quiser trocar o método atual ou configurar uma alternativa.",
                color = AppColors.TextSecondary,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(10.dp))
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onAccessibility) {
                Text(
                    if (capability.accessibilityServiceEnabled) {
                        "Acessibilidade · configurada"
                    } else "Configurar Acessibilidade"
                )
            }
            Spacer(Modifier.height(7.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = capability.canDrawOverlays || !capability.isLowRamDevice,
                onClick = onSystemOverlay
            ) {
                Text(
                    when {
                        capability.canDrawOverlays -> "Sobreposição normal · disponível"
                        capability.isLowRamDevice -> "Sobreposição normal indisponível"
                        else -> "Configurar sobreposição normal"
                    }
                )
            }
            Spacer(Modifier.height(7.dp))
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onNotification) {
                Text(if (notificationsAllowed) "Usar modo de notificação" else "Configurar notificações")
            }
        }
    }
}
