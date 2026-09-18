package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
        ready -> "Pronta para usar"
        else -> "Configuração necessária"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Relógio sobre o jogo",
                        color = AppColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Controle principal da janela flutuante",
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    color = when {
                        enabled -> AppColors.Success.copy(alpha = 0.13f)
                        ready -> AppColors.Accent.copy(alpha = 0.15f)
                        else -> AppColors.Warning.copy(alpha = 0.12f)
                    },
                    shape = CircleShape
                ) {
                    Text(
                        text = if (enabled) "ATIVO" else if (ready) "PRONTO" else "AJUSTAR",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = when {
                            enabled -> AppColors.Success
                            ready -> AppColors.AccentSoft
                            else -> AppColors.Warning
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                status,
                color = if (ready || enabled) AppColors.Success else AppColors.Warning,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(14.dp))
            Text("Mostrar no overlay", color = AppColors.TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            OverlayModeSelector(mode, onModeChange)
            Spacer(Modifier.height(16.dp))

            when {
                enabled -> OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDisable,
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Desativar janela") }
                ready -> Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEnableRecommended,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) { Text("Ativar relógio sobre o jogo") }
                else -> Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenSetup,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) { Text("Configurar em 2 passos") }
            }
        }
    }
}
