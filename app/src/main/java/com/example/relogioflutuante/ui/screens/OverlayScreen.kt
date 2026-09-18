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
import com.example.relogioflutuante.state.OverlayAppearanceState
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.ui.OverlayActivationController
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
    onOpenSetup: () -> Unit
) {
    val context = LocalContext.current
    val capability = remember(controller.permissionRefresh) { OverlayCapabilityDetector.read(context) }
    val notificationsAllowed = remember(controller.permissionRefresh) {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 12.dp, bottom = 20.dp)
    ) {
        OverlayPrimaryStatusCard(
            capability = capability,
            enabled = runtimeState.enabled,
            presentation = runtimeState.presentation,
            mode = mode,
            onModeChange = {
                mode = it
                OverlayState.setMode(context, it)
            },
            onEnableRecommended = controller.enableRecommendedOverlay,
            onOpenSetup = onOpenSetup,
            onDisable = {
                OverlayState.setEnabled(context, false)
                context.stopService(Intent(context, OverlayService::class.java))
            }
        )

        Spacer(Modifier.height(10.dp))
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
            onLockedChange = {
                OverlayAppearanceState.setPositionLocked(context, it)
                appearance = OverlayAppearanceState.read(context)
            }
        )

        if (ready) {
            Spacer(Modifier.height(10.dp))
            OverlayMethodOptionsCard(
                capability = capability,
                notificationsAllowed = notificationsAllowed,
                onAccessibility = controller.openAccessibilitySettings,
                onSystemOverlay = controller.openSystemOverlaySettings,
                onNotification = controller.enableNotificationMode
            )
        }

        Spacer(Modifier.height(10.dp))
        InfoCard(
            when {
                !ready -> "Primeiro toque em “Configurar em 2 passos”. Depois disso, ativar a janela passa a ser um único toque."
                appearance.positionLocked -> "Posição bloqueada: a janela não recebe toques e não interfere no jogo. Desbloqueie aqui quando quiser mover ou fechar."
                else -> "Arraste a janela para posicioná-la. Depois bloqueie a posição para que todos os toques continuem indo para o jogo."
            }
        )
    }
}
