package com.example.relogioflutuante.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.overlay.OverlayCapabilityDetector
import com.example.relogioflutuante.overlay.OverlayService
import com.example.relogioflutuante.state.OverlayMode
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.OverlayControlCard
import kotlinx.coroutines.delay

private data class OverlayRuntimeState(
    val enabled: Boolean,
    val presentation: OverlayPresentation
)

@Composable
fun OverlayScreen(
    permissionRefresh: Int,
    onEnableSystemOverlay: () -> Unit,
    onEnableAccessibilityOverlay: () -> Unit,
    onEnableNotificationMode: () -> Unit
) {
    val context = LocalContext.current
    val capability = remember(permissionRefresh) { OverlayCapabilityDetector.read(context) }
    val notificationsAllowed = remember(permissionRefresh) {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }
    var mode by remember { mutableStateOf(OverlayState.mode(context)) }

    val runtimeState by produceState(
        initialValue = OverlayRuntimeState(
            OverlayState.isEnabled(context),
            OverlayState.presentation(context)
        )
    ) {
        while (true) {
            value = OverlayRuntimeState(
                OverlayState.isEnabled(context),
                OverlayState.presentation(context)
            )
            delay(1_000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 14.dp, bottom = 20.dp)
    ) {
        OverlayControlCard(
            capability = capability,
            notificationsAllowed = notificationsAllowed,
            enabled = runtimeState.enabled,
            presentation = runtimeState.presentation,
            mode = mode,
            onModeChange = {
                mode = it
                OverlayState.setMode(context, it)
            },
            onEnableSystemOverlay = onEnableSystemOverlay,
            onEnableAccessibilityOverlay = onEnableAccessibilityOverlay,
            onEnableNotificationMode = onEnableNotificationMode,
            onDisable = {
                OverlayState.setEnabled(context, false)
                context.stopService(Intent(context, OverlayService::class.java))
            }
        )

        Spacer(Modifier.height(12.dp))
        InfoCard(
            if (capability.isLowRamDevice && !capability.canDrawOverlays) {
                "A opção por Acessibilidade cria a pequena janela sem usar a permissão ‘Sobrepor a outros apps’. Este serviço não lê o conteúdo da tela e não executa cliques ou gestos; ele só desenha o relógio ou a contagem."
            } else {
                "A janela só captura toque dentro do próprio relógio para arrastar ou fechar. O restante da tela e do jogo continua recebendo os toques normalmente."
            }
        )
    }
}
