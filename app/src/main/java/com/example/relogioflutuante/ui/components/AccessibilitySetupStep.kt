package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun AccessibilitySetupStep(
    showRestrictedSettingsReview: Boolean,
    onOpenAccessibility: () -> Unit,
    onReviewStepOne: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                if (showRestrictedSettingsReview) "Passo 2 de 2" else "Ativação",
                color = AppColors.AccentSoft,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Ativar relógio sobre os apps",
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(7.dp))
            Text(
                "Abra Acessibilidade e ative “Relógio Flutuante sobre apps”. O serviço só desenha a pequena janela; não lê a tela nem executa cliques.",
                color = AppColors.TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenAccessibility,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
            ) { Text("Abrir Acessibilidade") }
            if (showRestrictedSettingsReview) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onReviewStepOne
                ) { Text("Acessibilidade bloqueada? Rever passo 1") }
            }
            Spacer(Modifier.height(7.dp))
            Text(
                "Ao voltar, o aplicativo verifica automaticamente se foi ativado.",
                color = AppColors.TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
