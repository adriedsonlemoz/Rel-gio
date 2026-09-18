package com.example.relogioflutuante.state

enum class SetupPhase {
    RESTRICTED_SETTINGS,
    ACCESSIBILITY,
    READY
}

object SetupFlowResolver {
    fun resolve(
        accessibilityEnabled: Boolean,
        restrictedSettingsRequired: Boolean,
        restrictedSettingsConfirmed: Boolean
    ): SetupPhase = when {
        accessibilityEnabled -> SetupPhase.READY
        restrictedSettingsRequired && !restrictedSettingsConfirmed -> SetupPhase.RESTRICTED_SETTINGS
        else -> SetupPhase.ACCESSIBILITY
    }
}
