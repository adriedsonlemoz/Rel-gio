package com.example.relogioflutuante.state

enum class ClockOverlayAction {
    OPEN_RESTRICTED_SETTINGS,
    CONFIRM_RESTRICTED_SETTINGS,
    OPEN_ACCESSIBILITY,
    ACTIVATE_OVERLAY,
    ACTIVE
}

object ClockOverlayActionResolver {
    fun resolve(
        canDrawOverlays: Boolean,
        accessibilityEnabled: Boolean,
        restrictedSettingsConfirmed: Boolean,
        restrictedSettingsOpened: Boolean,
        floatingOverlayEnabled: Boolean
    ): ClockOverlayAction = when {
        floatingOverlayEnabled -> ClockOverlayAction.ACTIVE
        canDrawOverlays || accessibilityEnabled -> ClockOverlayAction.ACTIVATE_OVERLAY
        restrictedSettingsConfirmed -> ClockOverlayAction.OPEN_ACCESSIBILITY
        restrictedSettingsOpened -> ClockOverlayAction.CONFIRM_RESTRICTED_SETTINGS
        else -> ClockOverlayAction.OPEN_RESTRICTED_SETTINGS
    }
}
