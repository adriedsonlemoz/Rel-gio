package com.example.relogioflutuante.state

import org.junit.Assert.assertEquals
import org.junit.Test

class SetupFlowResolverTest {
    @Test
    fun startsWithRestrictedSettingsForSideloadedSetup() {
        assertEquals(
            SetupPhase.RESTRICTED_SETTINGS,
            SetupFlowResolver.resolve(accessibilityEnabled = false, restrictedSettingsConfirmed = false)
        )
    }

    @Test
    fun advancesToAccessibilityAfterManualConfirmation() {
        assertEquals(
            SetupPhase.ACCESSIBILITY,
            SetupFlowResolver.resolve(accessibilityEnabled = false, restrictedSettingsConfirmed = true)
        )
    }

    @Test
    fun accessibilityEnabledAlwaysMeansReady() {
        assertEquals(
            SetupPhase.READY,
            SetupFlowResolver.resolve(accessibilityEnabled = true, restrictedSettingsConfirmed = false)
        )
    }
}
