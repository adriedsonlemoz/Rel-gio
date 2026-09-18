package com.example.relogioflutuante.state

enum class OverlayPreset(
    val label: String,
    val timeFormat: OverlayTimeFormat,
    val size: OverlaySize,
    val opacityPercent: Int,
    val positionLocked: Boolean
) {
    GAME(
        label = "Jogo",
        timeFormat = OverlayTimeFormat.FULL,
        size = OverlaySize.MEDIUM,
        opacityPercent = 82,
        positionLocked = true
    ),
    DISCREET(
        label = "Discreto",
        timeFormat = OverlayTimeFormat.MINUTES_SECONDS,
        size = OverlaySize.SMALL,
        opacityPercent = 62,
        positionLocked = true
    ),
    COUNTDOWN(
        label = "Cronômetro",
        timeFormat = OverlayTimeFormat.SECONDS_ONLY,
        size = OverlaySize.LARGE,
        opacityPercent = 92,
        positionLocked = false
    )
}
