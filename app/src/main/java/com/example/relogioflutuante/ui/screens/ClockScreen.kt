package com.example.relogioflutuante.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.relogioflutuante.overlay.OverlayCapabilityDetector
import com.example.relogioflutuante.state.ClockOverlayAction
import com.example.relogioflutuante.state.ClockOverlayActionResolver
import com.example.relogioflutuante.state.ClockState
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.state.SetupGuideState
import com.example.relogioflutuante.ui.OverlayActivationController
import com.example.relogioflutuante.ui.components.ClockOverlayActionCard
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.TimeAdjustDialog
import com.example.relogioflutuante.ui.components.TimeCard
import com.example.relogioflutuante.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun ClockScreen(
    permissionRefresh: Int,
    controller: OverlayActivationController,
    onOpenSetup: () -> Unit
) {
    val context = LocalContext.current
    var showAdjust by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var overlayRefresh by remember { mutableIntStateOf(0) }

    val isSystemTime = remember(refreshKey) { ClockState.offsetMillis(context) == 0L }
    val capability = remember(permissionRefresh, overlayRefresh) {
        OverlayCapabilityDetector.read(context)
    }
    val restrictedConfirmed = remember(permissionRefresh, overlayRefresh) {
        SetupGuideState.isRestrictedSettingsConfirmed(context)
    }
    val restrictedOpened = remember(permissionRefresh, overlayRefresh) {
        SetupGuideState.isRestrictedSettingsOpened(context)
    }
    val floatingOverlayEnabled = remember(permissionRefresh, overlayRefresh, capability) {
        isFloatingOverlayReallyEnabled(context, capability)
    }
    val overlayAction = ClockOverlayActionResolver.resolve(
        canDrawOverlays = capability.canDrawOverlays,
        accessibilityEnabled = capability.accessibilityServiceEnabled,
        restrictedSettingsConfirmed = restrictedConfirmed,
        restrictedSettingsOpened = restrictedOpened,
        floatingOverlayEnabled = floatingOverlayEnabled
    )

    val displayedTime by produceState(
        initialValue = ClockState.formattedTime(context),
        key1 = refreshKey
    ) {
        while (true) {
            value = ClockState.formattedTime(context)
            val untilNextSecond = 1_000L - (System.currentTimeMillis() % 1_000L)
            delay(untilNextSecond.coerceAtLeast(100L))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(top = 12.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimeCard(
            title = "HORÁRIO",
            time = displayedTime,
            subtitle = if (isSystemTime) {
                "Sincronizado com o horário do aparelho"
            } else {
                "Horário ajustado somente dentro deste aplicativo"
            }
        )

        Spacer(Modifier.height(12.dp))
        ClockOverlayActionCard(
            action = overlayAction,
            onPrimaryAction = {
                handleOverlayAction(
                    action = overlayAction,
                    controller = controller,
                    onStateChanged = { overlayRefresh++ },
                    markRestrictedOpened = {
                        SetupGuideState.setRestrictedSettingsOpened(context, true)
                    },
                    confirmRestricted = {
                        SetupGuideState.setRestrictedSettingsConfirmed(context, true)
                    }
                )
            },
            onOpenFullGuide = onOpenSetup
        )

        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { showAdjust = true },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
            ) { Text("Ajustar horário") }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    ClockState.resetToSystemTime(context)
                    refreshKey++
                }
            ) { Text("Usar sistema") }
        }

        Spacer(Modifier.height(12.dp))
        InfoCard(
            "O ajuste não altera o relógio do Android. Ele muda apenas a hora exibida por este aplicativo e pelo relógio flutuante."
        )
    }

    if (showAdjust) {
        val shown = ClockState.displayedLocalTime(context)
        TimeAdjustDialog(
            initialHour = shown.hour,
            initialMinute = shown.minute,
            initialSecond = shown.second,
            onDismiss = { showAdjust = false },
            onConfirm = { hour, minute, second ->
                ClockState.setDisplayedTime(context, hour, minute, second)
                refreshKey++
                showAdjust = false
            }
        )
    }
}

private fun isFloatingOverlayReallyEnabled(
    context: android.content.Context,
    capability: com.example.relogioflutuante.overlay.OverlayCapability
): Boolean {
    if (!OverlayState.isEnabled(context)) return false
    return when (OverlayState.presentation(context)) {
        OverlayPresentation.SYSTEM_OVERLAY -> capability.canDrawOverlays
        OverlayPresentation.ACCESSIBILITY_OVERLAY -> capability.accessibilityServiceEnabled
        OverlayPresentation.NOTIFICATION -> false
    }
}

private fun handleOverlayAction(
    action: ClockOverlayAction,
    controller: OverlayActivationController,
    onStateChanged: () -> Unit,
    markRestrictedOpened: () -> Unit,
    confirmRestricted: () -> Unit
) {
    when (action) {
        ClockOverlayAction.OPEN_RESTRICTED_SETTINGS -> {
            markRestrictedOpened()
            controller.openAppDetails()
        }
        ClockOverlayAction.CONFIRM_RESTRICTED_SETTINGS -> {
            confirmRestricted()
            controller.enableAccessibilityOverlay()
        }
        ClockOverlayAction.OPEN_ACCESSIBILITY -> controller.enableAccessibilityOverlay()
        ClockOverlayAction.ACTIVATE_OVERLAY -> {
            controller.enableRecommendedOverlay()
            onStateChanged()
        }
        ClockOverlayAction.ACTIVE -> {
            controller.disableOverlay()
            onStateChanged()
        }
    }
}
