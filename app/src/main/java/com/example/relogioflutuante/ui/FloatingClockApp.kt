package com.example.relogioflutuante.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.relogioflutuante.state.FirstRunState
import com.example.relogioflutuante.ui.components.AppHeader
import com.example.relogioflutuante.ui.components.MainBottomNavigation
import com.example.relogioflutuante.ui.components.MainSection
import com.example.relogioflutuante.ui.dialogs.AboutDialog
import com.example.relogioflutuante.ui.layout.ScreenLayoutRules
import com.example.relogioflutuante.ui.layout.ScreenWidthClass
import com.example.relogioflutuante.ui.screens.AlarmScreen
import com.example.relogioflutuante.ui.screens.ClockScreen
import com.example.relogioflutuante.ui.screens.CountdownScreen
import com.example.relogioflutuante.ui.screens.OverlayScreen
import com.example.relogioflutuante.ui.screens.SetupScreen
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun FloatingClockApp(permissionRefresh: Int) {
    val context = LocalContext.current
    var section by rememberSaveable { mutableStateOf(MainSection.CLOCK) }
    var showAbout by remember { mutableStateOf(false) }
    var showSetup by remember { mutableStateOf(false) }
    var setupFromMenu by remember { mutableStateOf(false) }
    val overlayActivation = rememberOverlayActivationController(permissionRefresh)

    if (showSetup) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .displayCutoutPadding()
        ) {
            SetupScreen(
                permissionRefresh = permissionRefresh,
                controller = overlayActivation,
                showBack = setupFromMenu,
                onBack = { showSetup = false },
                onFinish = {
                    FirstRunState.setSetupComplete(context, true)
                    showSetup = false
                    setupFromMenu = false
                }
            )
        }
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .displayCutoutPadding(),
        containerColor = AppColors.Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppHeader(
                onOpenSetup = {
                    setupFromMenu = true
                    showSetup = true
                },
                onOpenAbout = { showAbout = true }
            )
        },
        bottomBar = {
            MainBottomNavigation(section = section, onSectionChange = { section = it })
        }
    ) { innerPadding ->
        ResponsiveContent(Modifier.padding(innerPadding)) {
            when (section) {
                MainSection.CLOCK -> ClockScreen(
                    permissionRefresh = permissionRefresh,
                    controller = overlayActivation,
                    onOpenSetup = {
                        setupFromMenu = true
                        showSetup = true
                    }
                )
                MainSection.COUNTDOWN -> CountdownScreen()
                MainSection.ALARMS -> AlarmScreen(permissionRefresh)
                MainSection.OVERLAY -> OverlayScreen(overlayActivation) {
                    setupFromMenu = true
                    showSetup = true
                }
            }
        }
    }

    if (showAbout) AboutDialog(onDismiss = { showAbout = false })
}

@Composable
private fun ResponsiveContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val widthDp = maxWidth.value.toInt()
        val widthClass = ScreenLayoutRules.widthClass(widthDp)
        val horizontal = ScreenLayoutRules.horizontalPaddingDp(widthDp).dp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontal),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = if (widthClass == ScreenWidthClass.WIDE) {
                    Modifier.width(700.dp)
                } else {
                    Modifier.fillMaxWidth()
                }
            ) {
                content()
            }
        }
    }
}
