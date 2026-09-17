package com.example.relogioflutuante.overlay

import com.example.relogioflutuante.state.OverlayPresentation
import org.junit.Assert.assertEquals
import org.junit.Test

class OverlayStrategyResolverTest {
    @Test
    fun prefersSystemOverlayWhenAvailable() {
        val capability = OverlayCapability(
            canDrawOverlays = true,
            isLowRamDevice = false,
            accessibilityServiceEnabled = false
        )
        assertEquals(
            OverlayPresentation.SYSTEM_OVERLAY,
            OverlayStrategyResolver.recommended(capability)
        )
    }

    @Test
    fun fallsBackToAccessibilityWhenSystemOverlayIsUnavailable() {
        val capability = OverlayCapability(
            canDrawOverlays = false,
            isLowRamDevice = true,
            accessibilityServiceEnabled = false
        )
        assertEquals(
            OverlayPresentation.ACCESSIBILITY_OVERLAY,
            OverlayStrategyResolver.recommended(capability)
        )
    }
}
