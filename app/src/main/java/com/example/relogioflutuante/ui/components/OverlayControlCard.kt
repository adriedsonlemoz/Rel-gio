package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
fun OverlayControlCard(
    capability: OverlayCapability,
    notificationsAllowed: Boolean,
    enabled: Boolean,
    presentation: OverlayPresentation,
    mode: OverlayMode,
    onModeChange: (OverlayMode) -> Unit,
    onEnableSystemOverlay: () -> Unit,
    onEnableAccessibilityOverlay: () -> Unit,
    onEnableNotificationMode: () -> Unit,
    onDisable: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Sobre outros aplicativos",
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (capability.isLowRamDevice && !capability.canDrawOverlays) {
                    "A permissão tradicional está bloqueada. Use a sobreposição por Acessibilidade para manter os segundos visíveis dentro do jogo."
                } else {
                    "Use a janela compacta sobre outros aplicativos. Acessibilidade fica disponível como alternativa."
                },
                color = AppColors.TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(14.dp))
            Text("Mostrar", color = AppColors.TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
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

            Spacer(Modifier.height(14.dp))
            StatusLine(
                label = "Sobreposição normal",
                value = when {
                    capability.canDrawOverlays -> "Disponível"
                    capability.isLowRamDevice -> "Bloqueada pelo sistema"
                    else -> "Permissão necessária"
                },
                good = capability.canDrawOverlays
            )
            Spacer(Modifier.height(7.dp))
            StatusLine(
                label = "Acessibilidade",
                value = if (capability.accessibilityServiceEnabled) "Ativada" else "Desativada",
                good = capability.accessibilityServiceEnabled
            )
            Spacer(Modifier.height(7.dp))
            StatusLine(
                label = "Notificações",
                value = if (notificationsAllowed) "Permitidas" else "Permissão necessária",
                good = notificationsAllowed
            )
            Spacer(Modifier.height(7.dp))
            StatusLine(
                label = "Estado",
                value = when {
                    !enabled -> "Desativado"
                    presentation == OverlayPresentation.ACCESSIBILITY_OVERLAY -> "Janela via Acessibilidade"
                    presentation == OverlayPresentation.NOTIFICATION -> "Somente notificação"
                    else -> "Janela normal ativa"
                },
                good = enabled
            )

            Spacer(Modifier.height(16.dp))
            if (!enabled) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = when {
                        capability.canDrawOverlays -> onEnableSystemOverlay
                        capability.isLowRamDevice -> onEnableAccessibilityOverlay
                        else -> onEnableSystemOverlay
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) {
                    Text(
                        when {
                            capability.canDrawOverlays -> "Ativar janela flutuante"
                            capability.isLowRamDevice && capability.accessibilityServiceEnabled -> "Ativar via Acessibilidade"
                            capability.isLowRamDevice -> "Configurar Acessibilidade"
                            else -> "Conceder permissão e ativar"
                        }
                    )
                }

                if (!capability.canDrawOverlays && capability.isLowRamDevice) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onEnableNotificationMode
                    ) {
                        Text("Usar somente notificação")
                    }
                } else if (!capability.canDrawOverlays || capability.accessibilityServiceEnabled) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onEnableAccessibilityOverlay
                    ) {
                        Text("Usar Acessibilidade")
                    }
                }
            } else {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDisable
                ) {
                    Text("Desativar")
                }
            }
        }
    }
}
