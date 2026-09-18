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
fun RestrictedSettingsStep(
    onOpenAppDetails: () -> Unit,
    onContinue: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Passo 1 de 2", color = AppColors.AccentSoft, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(
                "Permitir configurações restritas",
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(7.dp))
            Text(
                "Na tela que abrir, aguarde alguns segundos e toque em ⋮ no canto superior direito. Escolha “Permitir configurações restritas” e confirme.",
                color = AppColors.TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Se a opção ainda não aparecer ou não aceitar o toque, permaneça um pouco na tela, abra o menu ⋮ novamente ou volte aqui e abra as Informações do app outra vez.",
                color = AppColors.Warning,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
            Spacer(Modifier.height(12.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenAppDetails,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
            ) { Text("Abrir informações do app") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onContinue
            ) { Text("Já permiti · ir para Acessibilidade") }
        }
    }
}
