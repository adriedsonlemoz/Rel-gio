package com.example.relogioflutuante.ui.components.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        color = AppColors.Warning.copy(alpha = 0.09f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 13.dp, end = 8.dp, top = 9.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("!", color = AppColors.Warning, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    permissionTitle(exactAllowed, notificationsAllowed),
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    permissionMessage(exactAllowed, notificationsAllowed),
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
            TextButton(onClick = if (!exactAllowed) onRequestExact else onRequestNotifications) {
                Text("Corrigir", color = AppColors.AccentSoft)
            }
        }
    }
}

private fun permissionTitle(exactAllowed: Boolean, notificationsAllowed: Boolean): String = when {
    !exactAllowed -> "Alarme exato não autorizado"
    !notificationsAllowed -> "Notificações desativadas"
    else -> "Permissões"
}

private fun permissionMessage(exactAllowed: Boolean, notificationsAllowed: Boolean): String = when {
    !exactAllowed && !notificationsAllowed -> "Libere a precisão; depois confirme as notificações."
    !exactAllowed -> "Sem isso, o Android pode atrasar o disparo."
    else -> "Necessárias para mostrar o alerta e o botão Parar."
}
