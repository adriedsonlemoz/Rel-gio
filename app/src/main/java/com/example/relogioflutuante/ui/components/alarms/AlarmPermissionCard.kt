package com.example.relogioflutuante.ui.components.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun AlarmPermissionCard(
    exactAllowed: Boolean,
    notificationsAllowed: Boolean,
    onRequestExact: () -> Unit,
    onRequestNotifications: () -> Unit
) {
    if (exactAllowed && notificationsAllowed) return
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Permissões dos alarmes",
                    modifier = Modifier.fillMaxWidth(),
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    permissionMessage(exactAllowed, notificationsAllowed),
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            if (!exactAllowed) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRequestExact,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) { Text("Permitir alarme exato") }
            }
            if (!notificationsAllowed) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRequestNotifications,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.SurfaceStrong)
                ) { Text("Permitir notificações") }
            }
        }
    }
}

private fun permissionMessage(exactAllowed: Boolean, notificationsAllowed: Boolean): String = when {
    !exactAllowed && !notificationsAllowed ->
        "Libere alarmes exatos para tocar no horário definido e notificações para exibir o alerta e o botão Parar."
    !exactAllowed ->
        "Sem acesso a alarmes exatos, o Android pode atrasar o disparo. O alarme continua agendado em modo compatível."
    else ->
        "Libere notificações para visualizar o alerta do alarme e o botão Parar."
}
