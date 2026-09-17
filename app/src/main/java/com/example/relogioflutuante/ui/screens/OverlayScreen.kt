package com.example.relogioflutuante.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.overlay.OverlayService
import com.example.relogioflutuante.state.OverlayMode
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.StatusLine
import com.example.relogioflutuante.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun OverlayScreen(
    permissionRefresh: Int,
    onEnableOverlay: () -> Unit
) {
    val context = LocalContext.current
    val canOverlay = remember(permissionRefresh) { Settings.canDrawOverlays(context) }
    var mode by remember { mutableStateOf(OverlayState.mode(context)) }

    val overlayEnabled by produceState(initialValue = OverlayState.isEnabled(context)) {
        while (true) {
            value = OverlayState.isEnabled(context)
            delay(500L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 18.dp, bottom = 24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    "Relógio sobre outros apps",
                    color = AppColors.TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "A janela é compacta, pode ser arrastada e não bloqueia os toques fora dela.",
                    color = AppColors.TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(18.dp))
                Text("Mostrar no overlay", color = AppColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = mode == OverlayMode.CLOCK,
                        onClick = {
                            mode = OverlayMode.CLOCK
                            OverlayState.setMode(context, mode)
                        },
                        label = { Text("Relógio") }
                    )
                    FilterChip(
                        selected = mode == OverlayMode.COUNTDOWN,
                        onClick = {
                            mode = OverlayMode.COUNTDOWN
                            OverlayState.setMode(context, mode)
                        },
                        label = { Text("Contagem") }
                    )
                }

                Spacer(Modifier.height(18.dp))
                StatusLine(
                    label = "Permissão sobre outros apps",
                    value = if (canOverlay) "Permitida" else "Necessária",
                    good = canOverlay
                )
                Spacer(Modifier.height(8.dp))
                StatusLine(
                    label = "Overlay",
                    value = if (overlayEnabled) "Ativo" else "Desativado",
                    good = overlayEnabled
                )

                Spacer(Modifier.height(18.dp))
                if (!overlayEnabled) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onEnableOverlay,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                    ) {
                        Text(if (canOverlay) "Ativar sobreposição" else "Conceder permissão e ativar")
                    }
                } else {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            context.startService(
                                Intent(context, OverlayService::class.java)
                                    .setAction(OverlayService.ACTION_STOP)
                            )
                        }
                    ) {
                        Text("Desativar sobreposição")
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        InfoCard(
            "Enquanto o overlay estiver ativo, um serviço em primeiro plano mantém a janela funcionando mesmo depois que você sair do aplicativo."
        )
    }
}
