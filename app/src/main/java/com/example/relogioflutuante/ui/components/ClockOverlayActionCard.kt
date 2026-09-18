package com.example.relogioflutuante.ui.components

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.state.ClockOverlayAction
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun ClockOverlayActionCard(
    action: ClockOverlayAction,
    onPrimaryAction: () -> Unit,
    onOpenFullGuide: () -> Unit
) {
    val copy = ClockOverlayActionCopy.forAction(action)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Relógio sobre o jogo",
                        color = AppColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        copy.status,
                        color = copy.statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(7.dp))
            Text(
                copy.description,
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
            Spacer(Modifier.height(11.dp))
            if (action == ClockOverlayAction.ACTIVE) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onPrimaryAction
                ) { Text(copy.buttonLabel) }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onPrimaryAction,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) { Text(copy.buttonLabel) }
            }
            if (action != ClockOverlayAction.ACTIVE) {
                Spacer(Modifier.height(5.dp))
                androidx.compose.material3.TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenFullGuide
                ) { Text("Ver guia completo") }
            }
        }
    }
}

private data class ClockOverlayActionCopy(
    val status: String,
    val description: String,
    val buttonLabel: String,
    val statusColor: androidx.compose.ui.graphics.Color
) {
    companion object {
        fun forAction(action: ClockOverlayAction): ClockOverlayActionCopy = when (action) {
            ClockOverlayAction.OPEN_RESTRICTED_SETTINGS -> ClockOverlayActionCopy(
                "Configuração necessária",
                "Primeiro libere as configurações restritas do APK. Na tela do Android, use ⋮ > Permitir configurações restritas.",
                "1. Liberar configurações",
                AppColors.Warning
            )
            ClockOverlayAction.CONFIRM_RESTRICTED_SETTINGS -> ClockOverlayActionCopy(
                "Passo 1 aberto",
                "Se você já tocou em “Permitir configurações restritas”, continue para a Acessibilidade.",
                "Já permiti · continuar",
                AppColors.Warning
            )
            ClockOverlayAction.OPEN_ACCESSIBILITY -> ClockOverlayActionCopy(
                "Falta ativar Acessibilidade",
                "Ative “Relógio Flutuante sobre apps”. Ao voltar, a janela será ativada automaticamente.",
                "2. Ativar Acessibilidade",
                AppColors.Warning
            )
            ClockOverlayAction.ACTIVATE_OVERLAY -> ClockOverlayActionCopy(
                "Pronto para usar",
                "As permissões necessárias já estão disponíveis neste aparelho.",
                "Ativar sobre o jogo",
                AppColors.Success
            )
            ClockOverlayAction.ACTIVE -> ClockOverlayActionCopy(
                "Ativo",
                "O relógio flutuante está ligado e pode permanecer visível por cima do jogo.",
                "Desativar relógio flutuante",
                AppColors.Success
            )
        }
    }
}
