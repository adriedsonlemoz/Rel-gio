package com.example.relogioflutuante.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.overlay.OverlayService
import com.example.relogioflutuante.ui.screens.ClockScreen
import com.example.relogioflutuante.ui.screens.CountdownScreen
import com.example.relogioflutuante.ui.screens.OverlayScreen
import com.example.relogioflutuante.ui.theme.AppColors

private enum class MainSection(val label: String) {
    CLOCK("Relógio"),
    COUNTDOWN("Contagem"),
    OVERLAY("Sobreposição")
}

@Composable
fun FloatingClockApp() {
    val context = LocalContext.current
    var section by remember { mutableStateOf(MainSection.CLOCK) }
    var pendingOverlayEnable by remember { mutableStateOf(false) }
    var permissionRefresh by remember { mutableIntStateOf(0) }

    fun startOverlay() {
        ContextCompat.startForegroundService(context, Intent(context, OverlayService::class.java))
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        startOverlay()
    }

    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        permissionRefresh++
        if (pendingOverlayEnable && Settings.canDrawOverlays(context)) {
            pendingOverlayEnable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                startOverlay()
            }
        }
    }

    fun requestEnableOverlay() {
        if (!Settings.canDrawOverlays(context)) {
            pendingOverlayEnable = true
            overlayPermissionLauncher.launch(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startOverlay()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = AppColors.Background,
        topBar = { AppHeader() },
        bottomBar = {
            NavigationBar(containerColor = AppColors.Surface) {
                MainSection.entries.forEach { item ->
                    NavigationBarItem(
                        selected = section == item,
                        onClick = { section = item },
                        icon = { Text(if (section == item) "●" else "○", fontSize = 16.sp) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ResponsiveContent {
                when (section) {
                    MainSection.CLOCK -> ClockScreen()
                    MainSection.COUNTDOWN -> CountdownScreen()
                    MainSection.OVERLAY -> OverlayScreen(
                        permissionRefresh = permissionRefresh,
                        onEnableOverlay = ::requestEnableOverlay
                    )
                }
            }
        }
    }
}

@Composable
private fun AppHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = "Relógio Flutuante",
            color = AppColors.TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Hora e contagem sempre à vista",
            color = AppColors.TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ResponsiveContent(content: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val horizontal = if (maxWidth >= 700.dp) 48.dp else 18.dp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontal),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = if (maxWidth >= 700.dp) Modifier.width(680.dp) else Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}
