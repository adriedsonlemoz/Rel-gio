package com.example.relogioflutuante.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import com.example.relogioflutuante.state.SetupFlowResolver
import com.example.relogioflutuante.state.SetupGuideState
import com.example.relogioflutuante.state.SetupPhase
import com.example.relogioflutuante.ui.OverlayActivationController
import com.example.relogioflutuante.ui.components.AccessibilitySetupStep
import com.example.relogioflutuante.ui.components.AppScreenColumn
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
    val capability = controller.capability
    val restrictedConfirmed = remember(permissionRefresh) {
        SetupGuideState.isRestrictedSettingsConfirmed(context)
    }
    val restrictedSettingsRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val phase = SetupFlowResolver.resolve(
        accessibilityEnabled = capability.accessibilityServiceEnabled,
        restrictedSettingsRequired = restrictedSettingsRequired,
        restrictedSettingsConfirmed = restrictedConfirmed
    )

    AppScreenColumn(
        modifier = Modifier,
        horizontalPadding = 18.dp,
        horizontalAlignment = Alignment.Start
    ) {
        SetupHeader(
            showBack = showBack,
            restrictedSettingsRequired = restrictedSettingsRequired,
            onBack = onBack
        )

        when (phase) {
            SetupPhase.RESTRICTED_SETTINGS -> RestrictedSettingsStep(
                onOpenAppDetails = controller.openAppDetails,
                onContinue = {
                    SetupGuideState.setRestrictedSettingsConfirmed(context, true)
                    controller.openAccessibilitySettings()
                }
            )
            SetupPhase.ACCESSIBILITY -> AccessibilitySetupStep(
                showRestrictedSettingsReview = restrictedSettingsRequired,
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

        InfoCard(
            when (phase) {
                SetupPhase.RESTRICTED_SETTINGS -> "No Android 13 ou superior, APKs instalados fora da loja podem exigir essa liberação antes da Acessibilidade."
                SetupPhase.ACCESSIBILITY -> "Não é necessário configurar “Sobrepor a outros apps” neste aparelho se a Acessibilidade funcionar."
                SetupPhase.READY -> "Depois de concluído, essa configuração fica salva. Você só volta aqui se quiser trocar o método."
            }
        )
    }
}

@Composable
private fun SetupHeader(
    showBack: Boolean,
    restrictedSettingsRequired: Boolean,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Surface,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Voltar",
                        tint = AppColors.AccentSoft
                    )
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = if (showBack) 0.dp else 8.dp)) {
                Text(
                    "Ativar relógio flutuante",
                    color = AppColors.TextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (restrictedSettingsRequired) {
                        "São apenas dois passos na primeira configuração."
                    } else {
                        "Neste Android, basta ativar a Acessibilidade."
                    },
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
