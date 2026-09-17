package com.example.relogioflutuante.overlay

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

object OverlayCapabilityDetector {
    fun read(context: Context): OverlayCapability {
        val activityManager = context.getSystemService(ActivityManager::class.java)
        val lowRamFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_RAM_LOW)
        return OverlayCapability(
            canDrawOverlays = Settings.canDrawOverlays(context),
            isLowRamDevice = activityManager?.isLowRamDevice == true || lowRamFeature,
            accessibilityServiceEnabled = isAccessibilityServiceEnabled(context)
        )
    }

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val manager = context.getSystemService(AccessibilityManager::class.java) ?: return false
        val expected = ComponentName(context, AccessibilityOverlayService::class.java)
        return manager
            .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any { service ->
                val serviceInfo = service.resolveInfo.serviceInfo
                val className = if (serviceInfo.name.startsWith(".")) {
                    serviceInfo.packageName + serviceInfo.name
                } else {
                    serviceInfo.name
                }
                serviceInfo.packageName == expected.packageName && className == expected.className
            }
    }
}
