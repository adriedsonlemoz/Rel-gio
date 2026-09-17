package com.example.relogioflutuante.overlay

data class OverlayCapability(
    val canDrawOverlays: Boolean,
    val isLowRamDevice: Boolean,
    val accessibilityServiceEnabled: Boolean
)
