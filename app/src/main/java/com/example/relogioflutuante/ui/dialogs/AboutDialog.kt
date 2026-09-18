package com.example.relogioflutuante.ui.dialogs

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.relogioflutuante.alarms.AlarmScheduler
import com.example.relogioflutuante.state.readAppDiagnostics
import com.example.relogioflutuante.ui.SettingsNavigator
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
    onCopyPix: () -> Unit,
    onMessage: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val diagnostics = remember { readAppDiagnostics(context) }
    val navigator = remember(context) { SettingsNavigator(context) }
    val scheduler = remember(context) { AlarmScheduler(context) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).heightIn(max = 690.dp),
            shape = RoundedCornerShape(26.dp),
            color = AppColors.SurfaceStrong,
            shadowElevation = 18.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Column(
                    modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Relógio Flutuante", color = AppColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Versão ${diagnostics.version} • código ${diagnostics.versionCode}",
                        color = AppColors.AccentSoft,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Relógio, contagem, alarmes e overlay compacto para uso sobre outros aplicativos.",
                        color = AppColors.TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    ChangesCard()
                    DiagnosticCard(
                        diagnostics = diagnostics,
                        onCopy = {
                            clipboard.setText(AnnotatedString(diagnostics.asText()))
                            onMessage("Diagnóstico copiado")
                        },
                        onFixOverlay = navigator::openOverlayPermission,
                        onFixAccessibility = navigator::openAccessibility,
                        onFixExactAlarms = {
                            runCatching { context.startActivity(scheduler.exactAlarmSettingsIntent()) }
                                .onFailure { navigator.openAppDetails() }
                        },
                        onFixNotifications = {
                            context.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            )
                        }
                    )
                    PrivacyCard()
                    PixCard(onCopyPix)
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End).padding(top = 6.dp)
                ) { Text("Fechar") }
            }
        }
    }
}
