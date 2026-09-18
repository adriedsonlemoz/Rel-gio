package com.example.relogioflutuante.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.relogioflutuante.R
import com.example.relogioflutuante.overlay.OverlayCapabilityDetector
import com.example.relogioflutuante.state.SetupFlowResolver
import com.example.relogioflutuante.state.SetupGuideState
import com.example.relogioflutuante.state.SetupPhase
import com.example.relogioflutuante.ui.OverlayActivationController
import com.example.relogioflutuante.ui.components.AccessibilitySetupStep
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.RestrictedSettingsStep
import com.example.relogioflutuante.ui.components.SetupReadyCard
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
    val restrictedConfirmed = remember(permissionRefresh) {
        SetupGuideState.isRestrictedSettingsConfirmed(context)
    }
    val phase = SetupFlowResolver.resolve(
        accessibilityEnabled = capability.accessibilityServiceEnabled,
        restrictedSettingsConfirmed = restrictedConfirmed
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        SetupHeader(showBack = showBack, onBack = onBack)
        Spacer(Modifier.height(16.dp))

        when (phase) {
            SetupPhase.RESTRICTED_SETTINGS -> RestrictedSettingsStep(
                onOpenAppDetails = controller.openAppDetails,
                onContinue = {
                    SetupGuideState.setRestrictedSettingsConfirmed(context, true)
                    controller.openAccessibilitySettings()
                }
            )
            SetupPhase.ACCESSIBILITY -> AccessibilitySetupStep(
                onOpenAccessibility = controller.openAccessibilitySettings,
                onReviewStepOne = {
                    SetupGuideState.setRestrictedSettingsConfirmed(context, false)
                    controller.openAppDetails()
                }
            )
            SetupPhase.READY -> SetupReadyCard(
                onActivate = {
                    controller.enableAccessibilityOverlay()
                    onFinish()
                }
            )
        }

        Spacer(Modifier.height(12.dp))
        InfoCard(
            when (phase) {
                SetupPhase.RESTRICTED_SETTINGS -> "Essa liberação é exigida pelo Android para APKs instalados fora da Play Store."
                SetupPhase.ACCESSIBILITY -> "Não é necessário configurar “Sobrepor a outros apps” neste aparelho se a Acessibilidade funcionar."
                SetupPhase.READY -> "Depois de concluído, essa configuração fica salva. Você só volta aqui se quiser trocar o método."
            }
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SetupHeader(showBack: Boolean, onBack: () -> Unit) {
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
                "Ativar relógio flutuante",
                color = AppColors.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "São apenas dois passos na primeira configuração.",
                color = AppColors.TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}
