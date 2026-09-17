package com.example.relogioflutuante.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.R
import com.example.relogioflutuante.overlay.OverlayCapabilityDetector
import com.example.relogioflutuante.ui.OverlayActivationController
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.SetupStepCard
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun SetupScreen(
    permissionRefresh: Int,
    controller: OverlayActivationController,
    showBack: Boolean,
    onBack: () -> Unit,
    onFinish: () -> Unit
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
    val ready = capability.canDrawOverlays || capability.accessibilityServiceEnabled
    val preferredAccessibility = !capability.canDrawOverlays

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Voltar",
                        tint = AppColors.TextPrimary
                    )
                }
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "Configuração inicial",
                    color = AppColors.TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (preferredAccessibility) {
                        "Este aparelho usará preferencialmente o modo por Acessibilidade."
                    } else {
                        "Escolha o método de sobreposição que funcionar melhor no aparelho."
                    },
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        SetupStepCard(
            number = 1,
            title = "Permitir configurações restritas",
            description = "Como o APK foi instalado fora da Play Store, abra Informações do app, toque em ⋮ e escolha “Permitir configurações restritas”. O Android não permite que o app faça isso sozinho.",
            status = if (capability.accessibilityServiceEnabled) "Concluído" else "Manual",
            completed = capability.accessibilityServiceEnabled,
            buttonLabel = if (capability.accessibilityServiceEnabled) null else "Abrir informações do app",
            onClick = controller.openAppDetails
        )

        Spacer(Modifier.height(10.dp))
        SetupStepCard(
            number = 2,
            title = "Ativar Acessibilidade",
            description = "Ative “Relógio Flutuante sobre apps”. O serviço apenas desenha o relógio ou a contagem; não lê a tela e não executa cliques ou gestos.",
            status = if (capability.accessibilityServiceEnabled) "Ativada" else "Pendente",
            completed = capability.accessibilityServiceEnabled,
            buttonLabel = if (capability.accessibilityServiceEnabled) null else "Abrir Acessibilidade",
            primary = preferredAccessibility,
            onClick = controller.openAccessibilitySettings
        )

        Spacer(Modifier.height(10.dp))
        SetupStepCard(
            number = 3,
            title = "Sobreposição normal",
            description = when {
                capability.canDrawOverlays -> "A permissão tradicional está disponível e pode ser usada sem Acessibilidade."
                capability.isLowRamDevice -> "O sistema deste aparelho bloqueia esse método. Ele não é necessário se a Acessibilidade estiver ativa."
                else -> "Método alternativo usando a permissão “Sobrepor a outros apps”."
            },
            status = when {
                capability.canDrawOverlays -> "Disponível"
                capability.isLowRamDevice -> "Opcional"
                else -> "Não concedida"
            },
            completed = capability.canDrawOverlays || capability.isLowRamDevice,
            buttonLabel = if (!capability.canDrawOverlays && !capability.isLowRamDevice) {
                "Abrir sobreposição"
            } else null,
            onClick = controller.openSystemOverlaySettings
        )

        Spacer(Modifier.height(10.dp))
        SetupStepCard(
            number = 4,
            title = "Notificações",
            description = "Usadas somente pelo modo de compatibilidade e pelo serviço em primeiro plano. Não são necessárias para enxergar a janela por Acessibilidade.",
            status = if (notificationsAllowed) "Permitidas" else "Opcional",
            completed = notificationsAllowed,
            buttonLabel = if (notificationsAllowed) null else "Permitir notificações",
            onClick = controller.requestNotificationPermission
        )

        Spacer(Modifier.height(12.dp))
        InfoCard(
            "Ao voltar das Configurações do Android, esta tela verifica automaticamente o novo estado. Você não precisa reiniciar o aplicativo."
        )
        Spacer(Modifier.height(14.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = ready,
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
        ) {
            Text(if (ready) "Concluir configuração" else "Conclua um método de sobreposição")
        }
        Spacer(Modifier.height(16.dp))
    }
}
