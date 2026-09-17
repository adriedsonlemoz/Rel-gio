package com.example.relogioflutuante.overlay

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.example.relogioflutuante.state.ClockState
import com.example.relogioflutuante.state.CountdownState
import com.example.relogioflutuante.state.OverlayMode
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.state.formatDuration

class AccessibilityOverlayService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var windowController: OverlayWindowController

    private val ticker = object : Runnable {
        override fun run() {
            updateOverlay()
            scheduleNextTick()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowController = OverlayWindowController(
            context = this,
            onClose = {
                OverlayState.setEnabled(this, false)
                windowController.hide()
            },
            windowType = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            canShow = { true }
        )
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Intencionalmente vazio: o serviço não lê nem reage ao conteúdo de outros apps.
    }

    override fun onInterrupt() = Unit

    private fun updateOverlay() {
        if (!::windowController.isInitialized) return
        val shouldShow = OverlayState.isEnabled(this) &&
            OverlayState.presentation(this) == OverlayPresentation.ACCESSIBILITY_OVERLAY

        if (!shouldShow) {
            windowController.hide()
            return
        }

        if (!windowController.ensureVisible()) return
        when (OverlayState.mode(this)) {
            OverlayMode.CLOCK -> windowController.renderClock(ClockState.formattedTime(this))
            OverlayMode.COUNTDOWN -> {
                val snapshot = CountdownState.snapshot(this)
                windowController.renderCountdown(
                    formatDuration(snapshot.remainingMillis),
                    snapshot.isFinished
                )
            }
        }
    }

    private fun scheduleNextTick() {
        val now = System.currentTimeMillis()
        val delay = (1_000L - (now % 1_000L)).coerceIn(50L, 1_000L)
        handler.postDelayed(ticker, delay)
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        handler.removeCallbacks(ticker)
        if (::windowController.isInitialized) windowController.hide()
        if (OverlayState.presentation(this) == OverlayPresentation.ACCESSIBILITY_OVERLAY) {
            OverlayState.setEnabled(this, false)
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        if (::windowController.isInitialized) windowController.hide()
        super.onDestroy()
    }
}
