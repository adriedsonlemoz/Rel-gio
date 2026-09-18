package com.example.relogioflutuante.overlay

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.MainActivity
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState

class OverlayQuickTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        refreshTile()
    }

    override fun onClick() {
        super.onClick()
        if (OverlayState.isEnabled(this)) {
            disableOverlay()
        } else {
            enableOverlayOrOpenApp()
        }
        refreshTile()
    }

    private fun disableOverlay() {
        OverlayState.setEnabled(this, false)
        stopService(Intent(this, OverlayService::class.java))
    }

    private fun enableOverlayOrOpenApp() {
        val capability = OverlayCapabilityDetector.read(this)
        val preferred = OverlayState.presentation(this)
        when {
            preferred == OverlayPresentation.SYSTEM_OVERLAY && capability.canDrawOverlays -> {
                startForegroundOverlay(OverlayPresentation.SYSTEM_OVERLAY)
            }
            preferred == OverlayPresentation.ACCESSIBILITY_OVERLAY && capability.accessibilityServiceEnabled -> {
                enableAccessibilityOverlay()
            }
            preferred == OverlayPresentation.NOTIFICATION && notificationsAllowed() -> {
                startForegroundOverlay(OverlayPresentation.NOTIFICATION)
            }
            capability.canDrawOverlays -> startForegroundOverlay(OverlayPresentation.SYSTEM_OVERLAY)
            capability.accessibilityServiceEnabled -> enableAccessibilityOverlay()
            else -> openApp()
        }
    }

    private fun startForegroundOverlay(presentation: OverlayPresentation) {
        OverlayState.setPresentation(this, presentation)
        OverlayState.setEnabled(this, true)
        ContextCompat.startForegroundService(this, Intent(this, OverlayService::class.java))
    }

    private fun enableAccessibilityOverlay() {
        OverlayState.setPresentation(this, OverlayPresentation.ACCESSIBILITY_OVERLAY)
        OverlayState.setEnabled(this, true)
        stopService(Intent(this, OverlayService::class.java))
    }

    private fun notificationsAllowed(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    private fun openApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    private fun refreshTile() {
        qsTile?.apply {
            state = if (OverlayState.isEnabled(this@OverlayQuickTileService)) {
                Tile.STATE_ACTIVE
            } else {
                Tile.STATE_INACTIVE
            }
            label = "Relógio Flutuante"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                subtitle = if (state == Tile.STATE_ACTIVE) "Ativo" else "Toque para ativar"
            }
            updateTile()
        }
    }
}
