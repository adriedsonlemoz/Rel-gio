package com.example.relogioflutuante.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.overlay.OverlayService
import com.example.relogioflutuante.state.OverlayAppearanceState
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.ui.OverlayActivationController
import com.example.relogioflutuante.ui.components.AppScreenColumn
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.OverlayAppearanceCard
import com.example.relogioflutuante.ui.components.OverlayMethodOptionsCard
import com.example.relogioflutuante.ui.components.OverlayPrimaryStatusCard
import kotlinx.coroutines.delay

private data class OverlayRuntimeState(
    val enabled: Boolean,
    val presentation: OverlayPresentation
)

@Composable
fun OverlayScreen(
    controller: OverlayActivationController,
    onOpenSetup: () -> Unit,
    onMessage: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val capability = controller.capability
    val notificationsAllowed = remember(controller.permissionRefresh) {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }
    var mode by remember { mutableStateOf(OverlayState.mode(context)) }
    var appearance by remember { mutableStateOf(OverlayAppearanceState.read(context)) }
    val ready = capability.canDrawOverlays || capability.accessibilityServiceEnabled

    val runtimeState by produceState(
        initialValue = OverlayRuntimeState(OverlayState.isEnabled(context), OverlayState.presentation(context))
    ) {
        while (true) {
            val next = OverlayRuntimeState(OverlayState.isEnabled(context), OverlayState.presentation(context))
            if (value != next) value = next
            delay(1_000L)
        }
    }

    AppScreenColumn {
        OverlayPrimaryStatusCard(
            capability = capability,
            enabled = runtimeState.enabled,
            presentation = runtimeState.presentation,
            mode = mode,
            onModeChange = {
                mode = it
                OverlayState.setMode(context, it)
            },
            onEnableRecommended = {
                controller.enableRecommendedOverlay()
                onMessage("Relógio flutuante ativado")
            },
            onOpenSetup = onOpenSetup,
            onDisable = {
                OverlayState.setEnabled(context, false)
                context.stopService(Intent(context, OverlayService::class.java))
                onMessage("Relógio flutuante desativado")
            }
        )

        OverlayAppearanceCard(
            appearance = appearance,
            onFormatChange = {
                OverlayAppearanceState.setTimeFormat(context, it)
                appearance = OverlayAppearanceState.read(context)
            },
            onSizeChange = {
                OverlayAppearanceState.setSize(context, it)
                appearance = OverlayAppearanceState.read(context)
            },
            onOpacityChange = {
                OverlayAppearanceState.setOpacity(context, it)
                appearance = OverlayAppearanceState.read(context)
            },
            onLockedChange = { locked ->
                OverlayAppearanceState.setPositionLocked(context, locked)
                appearance = OverlayAppearanceState.read(context)
                onMessage(if (locked) "Posição bloqueada" else "Posição desbloqueada")
            }
        )

        if (ready) {
            OverlayMethodOptionsCard(
                capability = capability,
                notificationsAllowed = notificationsAllowed,
                onAccessibility = controller.openAccessibilitySettings,
                onSystemOverlay = controller.openSystemOverlaySettings,
                onNotification = controller.enableNotificationMode
            )
        }

        InfoCard(
            when {
                !ready -> "Primeiro toque em “Configurar em 2 passos”. Depois disso, ativar a janela passa a ser um único toque."
                appearance.positionLocked -> "Posição bloqueada: a janela não recebe toques e não interfere no jogo. Desbloqueie aqui quando quiser mover ou fechar."
                else -> "Arraste a janela para posicioná-la. Depois bloqueie a posição para que todos os toques continuem indo para o jogo."
            }
        )
    }
}
