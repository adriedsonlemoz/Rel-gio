package com.example.relogioflutuante.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.overlay.OverlayCapability
import com.example.relogioflutuante.overlay.OverlayCapabilityDetector
import com.example.relogioflutuante.overlay.OverlayService
import com.example.relogioflutuante.overlay.OverlayStrategyResolver
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState

data class OverlayActivationController(
    val permissionRefresh: Int,
    val capability: OverlayCapability,
    val enableRecommendedOverlay: () -> Unit,
    val enableSystemOverlay: () -> Unit,
    val enableAccessibilityOverlay: () -> Unit,
    val enableNotificationMode: () -> Unit,
    val disableOverlay: () -> Unit,
    val openAppDetails: () -> Unit,
    val openAccessibilitySettings: () -> Unit,
    val openSystemOverlaySettings: () -> Unit,
    val requestNotificationPermission: () -> Unit
)

@Composable
fun rememberOverlayActivationController(permissionRefresh: Int): OverlayActivationController {
    val context = LocalContext.current
    val navigator = remember(context) { SettingsNavigator(context) }
    var pendingActivation by remember { mutableStateOf<OverlayPresentation?>(null) }
    val capability = remember(permissionRefresh) { OverlayCapabilityDetector.read(context) }

    fun startForegroundPresentation(presentation: OverlayPresentation) {
        OverlayState.setPresentation(context, presentation)
        OverlayState.setEnabled(context, true)
        ContextCompat.startForegroundService(context, Intent(context, OverlayService::class.java))
    }

    fun activateAccessibilityOverlay() {
        OverlayState.setPresentation(context, OverlayPresentation.ACCESSIBILITY_OVERLAY)
        OverlayState.setEnabled(context, true)
        context.stopService(Intent(context, OverlayService::class.java))
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startForegroundPresentation(OverlayPresentation.NOTIFICATION)
    }

    fun requestNotifications() {
        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun enableSystemOverlay() {
        if (capability.canDrawOverlays) {
            pendingActivation = null
            startForegroundPresentation(OverlayPresentation.SYSTEM_OVERLAY)
        } else {
            pendingActivation = OverlayPresentation.SYSTEM_OVERLAY
            navigator.openOverlayPermission()
        }
    }

    fun enableAccessibilityOverlay() {
        if (capability.accessibilityServiceEnabled) {
            pendingActivation = null
            activateAccessibilityOverlay()
        } else {
            pendingActivation = OverlayPresentation.ACCESSIBILITY_OVERLAY
            navigator.openAccessibility()
        }
    }


    fun disableOverlay() {
        OverlayState.setEnabled(context, false)
        context.stopService(Intent(context, OverlayService::class.java))
    }

    fun enableNotificationMode() {
        val notificationsGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        if (notificationsGranted) {
            startForegroundPresentation(OverlayPresentation.NOTIFICATION)
        } else {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    LaunchedEffect(permissionRefresh, pendingActivation) {
        when (pendingActivation) {
            OverlayPresentation.SYSTEM_OVERLAY -> {
                if (capability.canDrawOverlays) {
                    pendingActivation = null
                    startForegroundPresentation(OverlayPresentation.SYSTEM_OVERLAY)
                }
            }
            OverlayPresentation.ACCESSIBILITY_OVERLAY -> {
                if (capability.accessibilityServiceEnabled) {
                    pendingActivation = null
                    activateAccessibilityOverlay()
                }
            }
            else -> Unit
        }
    }

    fun enableRecommended() {
        when (OverlayStrategyResolver.recommended(capability)) {
            OverlayPresentation.SYSTEM_OVERLAY -> enableSystemOverlay()
            OverlayPresentation.ACCESSIBILITY_OVERLAY -> enableAccessibilityOverlay()
            OverlayPresentation.NOTIFICATION -> enableNotificationMode()
        }
    }

    return OverlayActivationController(
        permissionRefresh = permissionRefresh,
        capability = capability,
        enableRecommendedOverlay = ::enableRecommended,
        enableSystemOverlay = ::enableSystemOverlay,
        enableAccessibilityOverlay = ::enableAccessibilityOverlay,
        enableNotificationMode = ::enableNotificationMode,
        disableOverlay = ::disableOverlay,
        openAppDetails = navigator::openAppDetails,
        openAccessibilitySettings = navigator::openAccessibility,
        openSystemOverlaySettings = navigator::openOverlayPermission,
        requestNotificationPermission = ::requestNotifications
    )
}
