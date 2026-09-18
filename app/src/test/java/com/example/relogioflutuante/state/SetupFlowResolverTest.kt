package com.example.relogioflutuante.state

import org.junit.Assert.assertEquals
import org.junit.Test

class SetupFlowResolverTest {
    @Test
    fun android13PlusStartsWithRestrictedSettingsWhenNeeded() {
        assertEquals(
            SetupPhase.RESTRICTED_SETTINGS,
            SetupFlowResolver.resolve(
                accessibilityEnabled = false,
                restrictedSettingsRequired = true,
                restrictedSettingsConfirmed = false
            )
        )
    }

    @Test
    fun android12AndEarlierGoDirectlyToAccessibility() {
        assertEquals(
            SetupPhase.ACCESSIBILITY,
            SetupFlowResolver.resolve(
                accessibilityEnabled = false,
                restrictedSettingsRequired = false,
                restrictedSettingsConfirmed = false
            )
        )
    }

    @Test
    fun advancesToAccessibilityAfterManualConfirmation() {
        assertEquals(
            SetupPhase.ACCESSIBILITY,
            SetupFlowResolver.resolve(
                accessibilityEnabled = false,
                restrictedSettingsRequired = true,
                restrictedSettingsConfirmed = true
            )
        )
    }

    @Test
    fun accessibilityEnabledAlwaysMeansReady() {
        assertEquals(
            SetupPhase.READY,
            SetupFlowResolver.resolve(
                accessibilityEnabled = true,
                restrictedSettingsRequired = true,
                restrictedSettingsConfirmed = false
            )
        )
    }
}
