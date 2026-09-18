package com.example.relogioflutuante.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
    var showSetup by remember { mutableStateOf(!FirstRunState.isSetupComplete(context)) }
    var setupFromMenu by remember { mutableStateOf(false) }
    val overlayActivation = rememberOverlayActivationController(permissionRefresh)

    if (showSetup) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .statusBarsPadding()
                .navigationBarsPadding()
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
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = AppColors.Background,
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
                MainSection.CLOCK -> ClockScreen()
                MainSection.COUNTDOWN -> CountdownScreen()
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
        val wide = maxWidth >= 700.dp
        val horizontal = if (wide) 48.dp else 18.dp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontal),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = if (wide) Modifier.width(680.dp) else Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}
