package com.example.relogioflutuante.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.overlay.OverlayCapabilityDetector
import com.example.relogioflutuante.overlay.OverlayService
import com.example.relogioflutuante.state.OverlayState
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.OverlayControlCard
import kotlinx.coroutines.delay

private data class OverlayRuntimeState(
    val enabled: Boolean,
    val notificationOnly: Boolean
)

@Composable
fun OverlayScreen(
    permissionRefresh: Int,
    onEnableOverlay: () -> Unit,
    onEnableCompatibleMode: () -> Unit
) {
    val context = LocalContext.current
    val capability = remember(permissionRefresh) { OverlayCapabilityDetector.read(context) }
    val notificationsAllowed = remember(permissionRefresh) {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }
    var mode by remember { mutableStateOf(OverlayState.mode(context)) }

    val runtimeState by produceState(
        initialValue = OverlayRuntimeState(
            OverlayState.isEnabled(context),
            OverlayState.notificationOnly(context)
        )
    ) {
        while (true) {
            value = OverlayRuntimeState(
                OverlayState.isEnabled(context),
                OverlayState.notificationOnly(context)
            )
            delay(1_000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 14.dp, bottom = 20.dp)
    ) {
        OverlayControlCard(
            capability = capability,
            notificationsAllowed = notificationsAllowed,
            enabled = runtimeState.enabled,
            notificationOnly = runtimeState.notificationOnly,
            mode = mode,
            onModeChange = {
                mode = it
                OverlayState.setMode(context, it)
            },
            onEnableOverlay = onEnableOverlay,
            onEnableCompatibleMode = onEnableCompatibleMode,
            onDisable = {
                context.startService(
                    Intent(context, OverlayService::class.java)
                        .setAction(OverlayService.ACTION_STOP)
                )
            }
        )

        Spacer(Modifier.height(12.dp))
        InfoCard(
            if (capability.shouldPreferCompatibleMode) {
                "No modo compatível, relógio ou contagem continuam funcionando fora do app pela notificação. Na contagem, a própria notificação oferece iniciar, pausar ou continuar."
            } else {
                "A janela flutuante não bloqueia toques fora dela. Se o Android impedir a sobreposição, use o modo compatível sem precisar de root ou ADB."
            }
        )
    }
}
