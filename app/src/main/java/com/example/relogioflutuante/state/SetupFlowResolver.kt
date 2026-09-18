package com.example.relogioflutuante.state

enum class SetupPhase {
    RESTRICTED_SETTINGS,
    ACCESSIBILITY,
    READY
}

object SetupFlowResolver {
    fun resolve(
        accessibilityEnabled: Boolean,
        restrictedSettingsConfirmed: Boolean
    ): SetupPhase = when {
        accessibilityEnabled -> SetupPhase.READY
        restrictedSettingsConfirmed -> SetupPhase.ACCESSIBILITY
        else -> SetupPhase.RESTRICTED_SETTINGS
    }
}
