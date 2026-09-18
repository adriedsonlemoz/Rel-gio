package com.example.relogioflutuante.state

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.alarms.AlarmScheduler
import com.example.relogioflutuante.overlay.OverlayCapabilityDetector

data class AppDiagnostics(
    val version: String,
    val versionCode: Long,
    val android: String,
    val overlayEnabled: Boolean,
    val overlayMethod: String,
    val drawOverlays: Boolean,
    val accessibility: Boolean,
    val exactAlarms: Boolean,
    val notifications: Boolean,
    val lowRamDevice: Boolean
) {
    fun asText(): String = buildString {
        appendLine("Relógio Flutuante - Diagnóstico")
        appendLine("Versão: $version ($versionCode)")
        appendLine("Android: $android")
        appendLine("Overlay: ${yesNo(overlayEnabled)}")
        appendLine("Método: $overlayMethod")
        appendLine("Sobrepor outros apps: ${yesNo(drawOverlays)}")
        appendLine("Acessibilidade: ${yesNo(accessibility)}")
        appendLine("Alarmes exatos: ${yesNo(exactAlarms)}")
        appendLine("Notificações: ${yesNo(notifications)}")
        append("Dispositivo low-RAM: ${yesNo(lowRamDevice)}")
    }

    private fun yesNo(value: Boolean) = if (value) "sim" else "não"
}

fun readAppDiagnostics(context: Context): AppDiagnostics {
    val capability = OverlayCapabilityDetector.read(context)
    val runtimeNotifications = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    val notifications = runtimeNotifications &&
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    return AppDiagnostics(
        version = appVersionName(context),
        versionCode = appVersionCode(context),
        android = "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
        overlayEnabled = OverlayState.isEnabled(context),
        overlayMethod = OverlayState.presentation(context).friendlyName(),
        drawOverlays = capability.canDrawOverlays,
        accessibility = capability.accessibilityServiceEnabled,
        exactAlarms = AlarmScheduler(context).canScheduleExact(),
        notifications = notifications,
        lowRamDevice = capability.isLowRamDevice
    )
}

private fun OverlayPresentation.friendlyName(): String = when (this) {
    OverlayPresentation.SYSTEM_OVERLAY -> "Sobreposição normal"
    OverlayPresentation.ACCESSIBILITY_OVERLAY -> "Acessibilidade"
    OverlayPresentation.NOTIFICATION -> "Notificação"
}
