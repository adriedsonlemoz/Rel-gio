package com.example.relogioflutuante.state

import org.junit.Assert.assertEquals
import org.junit.Test

class ClockOverlayActionResolverTest {
    @Test
    fun startsByOpeningRestrictedSettingsWhenNoOverlayMethodIsReady() {
        assertEquals(
            ClockOverlayAction.OPEN_RESTRICTED_SETTINGS,
            resolve()
        )
    }

    @Test
    fun afterOpeningAppDetailsAsksForManualConfirmation() {
        assertEquals(
            ClockOverlayAction.CONFIRM_RESTRICTED_SETTINGS,
            resolve(restrictedSettingsOpened = true)
        )
    }

    @Test
    fun confirmedRestrictedSettingsAdvancesToAccessibility() {
        assertEquals(
            ClockOverlayAction.OPEN_ACCESSIBILITY,
            resolve(restrictedSettingsConfirmed = true)
        )
    }

    @Test
    fun availableOverlayMethodCanBeActivatedImmediately() {
        assertEquals(
            ClockOverlayAction.ACTIVATE_OVERLAY,
            resolve(accessibilityEnabled = true)
        )
        assertEquals(
            ClockOverlayAction.ACTIVATE_OVERLAY,
            resolve(canDrawOverlays = true)
        )
    }

    @Test
    fun activeFloatingOverlayHasPriorityOverSetupSteps() {
        assertEquals(
            ClockOverlayAction.ACTIVE,
            resolve(floatingOverlayEnabled = true)
        )
    }

    private fun resolve(
        canDrawOverlays: Boolean = false,
        accessibilityEnabled: Boolean = false,
        restrictedSettingsConfirmed: Boolean = false,
        restrictedSettingsOpened: Boolean = false,
        floatingOverlayEnabled: Boolean = false
    ) = ClockOverlayActionResolver.resolve(
        canDrawOverlays = canDrawOverlays,
        accessibilityEnabled = accessibilityEnabled,
        restrictedSettingsConfirmed = restrictedSettingsConfirmed,
        restrictedSettingsOpened = restrictedSettingsOpened,
        floatingOverlayEnabled = floatingOverlayEnabled
    )
}
