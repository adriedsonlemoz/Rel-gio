package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.relogioflutuante.state.OverlayMode
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun OverlayPrimaryStatusCard(
    capability: OverlayCapability,
    enabled: Boolean,
    presentation: OverlayPresentation,
    mode: OverlayMode,
    onModeChange: (OverlayMode) -> Unit,
    onEnableRecommended: () -> Unit,
    onOpenSetup: () -> Unit,
    onDisable: () -> Unit
) {
    val ready = capability.canDrawOverlays || capability.accessibilityServiceEnabled
    val status = when {
        enabled && presentation == OverlayPresentation.ACCESSIBILITY_OVERLAY -> "Ativa via Acessibilidade"
        enabled && presentation == OverlayPresentation.SYSTEM_OVERLAY -> "Janela flutuante ativa"
        enabled -> "Modo por notificação ativo"
        capability.accessibilityServiceEnabled -> "Pronta para usar"
        capability.canDrawOverlays -> "Pronta para usar"
        else -> "Configuração necessária"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Relógio sobre o jogo", color = AppColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                status,
                color = if (ready || enabled) AppColors.Success else AppColors.Warning,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(14.dp))
            Text("Mostrar", color = AppColors.TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            OverlayModeSelector(mode, onModeChange)
            Spacer(Modifier.height(16.dp))

            when {
                enabled -> OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDisable
                ) { Text("Desativar janela") }
                ready -> Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEnableRecommended,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) { Text("Ativar relógio sobre o jogo") }
                else -> Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenSetup,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) { Text("Configurar em 2 passos") }
            }
        }
    }
}
