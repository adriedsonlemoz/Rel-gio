package com.example.relogioflutuante.overlay

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings

data class OverlayCapability(
    val canDrawOverlays: Boolean,
    val isLowRamDevice: Boolean
) {
    val shouldPreferCompatibleMode: Boolean
        get() = isLowRamDevice && !canDrawOverlays
}

object OverlayCapabilityDetector {
    fun read(context: Context): OverlayCapability {
        val activityManager = context.getSystemService(ActivityManager::class.java)
        val lowRamFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_RAM_LOW)
        return OverlayCapability(
            canDrawOverlays = Settings.canDrawOverlays(context),
            isLowRamDevice = activityManager?.isLowRamDevice == true || lowRamFeature
        )
    }
}
