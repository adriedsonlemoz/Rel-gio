package com.example.relogioflutuante.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.overlay.OverlayCapabilityDetector
import com.example.relogioflutuante.overlay.OverlayService
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState

data class OverlayActivationController(
    val permissionRefresh: Int,
    val enableSystemOverlay: () -> Unit,
    val enableAccessibilityOverlay: () -> Unit,
    val enableNotificationMode: () -> Unit
)

@Composable
fun rememberOverlayActivationController(): OverlayActivationController {
    val context = LocalContext.current
    var pendingSystemOverlayEnable by remember { mutableStateOf(false) }
    var pendingAccessibilityEnable by remember { mutableStateOf(false) }
    var pendingForegroundPresentation by remember {
        mutableStateOf(OverlayPresentation.SYSTEM_OVERLAY)
    }
    var permissionRefresh by remember { mutableIntStateOf(0) }

    fun startForegroundPresentation(presentation: OverlayPresentation) {
        OverlayState.setPresentation(context, presentation)
        ContextCompat.startForegroundService(context, Intent(context, OverlayService::class.java))
    }

    fun activateAccessibilityOverlay() {
        OverlayState.setPresentation(context, OverlayPresentation.ACCESSIBILITY_OVERLAY)
        OverlayState.setEnabled(context, true)
        context.stopService(Intent(context, OverlayService::class.java))
        permissionRefresh++
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionRefresh++
        if (granted || pendingForegroundPresentation == OverlayPresentation.SYSTEM_OVERLAY) {
            startForegroundPresentation(pendingForegroundPresentation)
        }
    }

    fun requestNotificationAndStart(presentation: OverlayPresentation) {
        pendingForegroundPresentation = presentation
        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED

        if (needsPermission) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startForegroundPresentation(presentation)
        }
    }

    val accessibilitySettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        permissionRefresh++
        if (pendingAccessibilityEnable) {
            pendingAccessibilityEnable = false
            if (OverlayCapabilityDetector.isAccessibilityServiceEnabled(context)) {
                activateAccessibilityOverlay()
            }
        }
    }

    fun requestEnableAccessibilityOverlay() {
        if (OverlayCapabilityDetector.isAccessibilityServiceEnabled(context)) {
            activateAccessibilityOverlay()
        } else {
            pendingAccessibilityEnable = true
            accessibilitySettingsLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    val systemOverlayPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        permissionRefresh++
        if (pendingSystemOverlayEnable) {
            pendingSystemOverlayEnable = false
            val capability = OverlayCapabilityDetector.read(context)
            if (capability.canDrawOverlays) {
                requestNotificationAndStart(OverlayPresentation.SYSTEM_OVERLAY)
            } else if (capability.isLowRamDevice) {
                requestEnableAccessibilityOverlay()
            }
        }
    }

    fun requestEnableSystemOverlay() {
        val capability = OverlayCapabilityDetector.read(context)
        when {
            capability.canDrawOverlays ->
                requestNotificationAndStart(OverlayPresentation.SYSTEM_OVERLAY)
            capability.isLowRamDevice -> requestEnableAccessibilityOverlay()
            else -> {
                pendingSystemOverlayEnable = true
                systemOverlayPermissionLauncher.launch(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                )
            }
        }
    }

    return OverlayActivationController(
        permissionRefresh = permissionRefresh,
        enableSystemOverlay = ::requestEnableSystemOverlay,
        enableAccessibilityOverlay = ::requestEnableAccessibilityOverlay,
        enableNotificationMode = {
            requestNotificationAndStart(OverlayPresentation.NOTIFICATION)
        }
    )
}
