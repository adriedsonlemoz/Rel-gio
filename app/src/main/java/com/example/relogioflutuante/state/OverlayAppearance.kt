package com.example.relogioflutuante.state

enum class OverlayTimeFormat {
    FULL,
    MINUTES_SECONDS,
    SECONDS_ONLY
}

enum class OverlaySize {
    SMALL,
    MEDIUM,
    LARGE
}

data class OverlayAppearance(
    val timeFormat: OverlayTimeFormat,
    val size: OverlaySize,
    val opacityPercent: Int,
    val positionLocked: Boolean
)
