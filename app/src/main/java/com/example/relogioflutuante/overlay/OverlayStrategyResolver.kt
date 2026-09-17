package com.example.relogioflutuante.overlay

import com.example.relogioflutuante.state.OverlayPresentation

object OverlayStrategyResolver {
    fun recommended(capability: OverlayCapability): OverlayPresentation = when {
        capability.canDrawOverlays -> OverlayPresentation.SYSTEM_OVERLAY
        else -> OverlayPresentation.ACCESSIBILITY_OVERLAY
    }
}
