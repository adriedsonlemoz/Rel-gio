package com.example.relogioflutuante.ui.screens

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.relogioflutuante.state.ClockOverlayAction
import com.example.relogioflutuante.state.ClockOverlayActionResolver
import com.example.relogioflutuante.state.ClockState
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.state.SetupGuideState
import com.example.relogioflutuante.ui.OverlayActivationController
import com.example.relogioflutuante.ui.components.AppScreenColumn
import com.example.relogioflutuante.ui.components.ClockOverlayActionCard
import com.example.relogioflutuante.ui.components.ClockTimeActions
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.TimeAdjustDialog
import com.example.relogioflutuante.ui.components.TimeCard
import com.example.relogioflutuante.ui.components.timezones.WorldClocksCard
import com.example.relogioflutuante.ui.dialogs.timezones.WorldClockPickerDialog
import com.example.relogioflutuante.ui.state.rememberWorldClockUiState
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId

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
    val worldClocks = rememberWorldClockUiState(context)

    val isSystemTime = remember(refreshKey) { ClockState.offsetMillis(context) == 0L }
    val capability = controller.capability
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
        restrictedSettingsRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,
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

    AppScreenColumn(imeAware = true) {
        TimeCard(
            title = "HORÁRIO",
            time = displayedTime,
            subtitle = if (isSystemTime) {
                "Sincronizado com o horário do aparelho"
            } else {
                "Horário ajustado somente dentro deste aplicativo"
            }
        )

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
            onRetryRestrictedSettings = {
                SetupGuideState.setRestrictedSettingsConfirmed(context, false)
                SetupGuideState.setRestrictedSettingsOpened(context, true)
                controller.openAppDetails()
                overlayRefresh++
            },
            onOpenFullGuide = onOpenSetup
        )

        ClockTimeActions(
            onAdjust = { showAdjust = true },
            onUseSystem = {
                ClockState.resetToSystemTime(context)
                refreshKey++
            }
        )

        WorldClocksCard(
            entries = worldClocks.entries,
            instant = Instant.ofEpochMilli(System.currentTimeMillis()),
            localZone = ZoneId.systemDefault(),
            onAdd = worldClocks::openPicker,
            onRemove = worldClocks::remove,
            onMoveUp = worldClocks::moveUp,
            onMoveDown = worldClocks::moveDown
        )

        InfoCard(
            "O ajuste não altera o relógio do Android. Ele muda apenas a hora exibida pelo app e pelo relógio flutuante."
        )
    }

    if (worldClocks.showPicker) {
        WorldClockPickerDialog(
            selectedZoneIds = worldClocks.zoneIds.toSet(),
            onAdd = worldClocks::add,
            onDismiss = worldClocks::closePicker
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
