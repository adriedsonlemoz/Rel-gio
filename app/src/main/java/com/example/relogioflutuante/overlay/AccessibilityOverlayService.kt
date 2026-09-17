package com.example.relogioflutuante.overlay

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.example.relogioflutuante.state.ClockState
import com.example.relogioflutuante.state.CountdownMath
import com.example.relogioflutuante.state.CountdownState
import com.example.relogioflutuante.state.OverlayAppearanceState
import com.example.relogioflutuante.state.OverlayMode
import com.example.relogioflutuante.state.OverlayPresentation
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.state.appPreferences

class AccessibilityOverlayService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var windowController: OverlayWindowController
    private lateinit var preferences: SharedPreferences

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    private val ticker = object : Runnable {
        override fun run() {
            val keepTicking = updateOverlay()
            if (keepTicking) scheduleNextTick()
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
        preferences = appPreferences()
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Intencionalmente vazio: o serviço não lê nem reage ao conteúdo de outros apps.
    }

    override fun onInterrupt() = Unit

    private fun updateOverlay(): Boolean {
        if (!::windowController.isInitialized) return false
        val shouldShow = OverlayState.isEnabled(this) &&
            OverlayState.presentation(this) == OverlayPresentation.ACCESSIBILITY_OVERLAY

        if (!shouldShow) {
            windowController.hide()
            return false
        }

        if (!windowController.ensureVisible()) return false
        when (OverlayState.mode(this)) {
            OverlayMode.CLOCK -> {
                val appearance = OverlayAppearanceState.read(this)
                windowController.renderClock(
                    OverlayTextFormatter.clock(
                        ClockState.displayedLocalTime(this),
                        appearance.timeFormat
                    )
                )
            }
            OverlayMode.COUNTDOWN -> {
                val snapshot = CountdownState.snapshot(this)
                val appearance = OverlayAppearanceState.read(this)
                windowController.renderCountdown(
                    OverlayTextFormatter.countdown(
                        snapshot.remainingMillis,
                        appearance.timeFormat
                    ),
                    snapshot.isFinished
                )
            }
        }
        return true
    }

    private fun scheduleNextTick() {
        handler.postDelayed(
            ticker,
            CountdownMath.delayUntilNextSecond(System.currentTimeMillis())
        )
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        cleanup()
        if (OverlayState.presentation(this) == OverlayPresentation.ACCESSIBILITY_OVERLAY) {
            OverlayState.setEnabled(this, false)
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        cleanup()
        super.onDestroy()
    }

    private fun cleanup() {
        handler.removeCallbacks(ticker)
        if (::preferences.isInitialized) {
            preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
        }
        if (::windowController.isInitialized) windowController.hide()
    }
}
