package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.overlay.OverlayCapability
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun OverlayMethodOptionsCard(
    capability: OverlayCapability,
    notificationsAllowed: Boolean,
    onAccessibility: () -> Unit,
    onSystemOverlay: () -> Unit,
    onNotification: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column {
            Column(Modifier.padding(horizontal = 15.dp, vertical = 12.dp)) {
                Text("Outros métodos", color = AppColors.TextPrimary, fontWeight = FontWeight.Bold)
                Text(
                    "Alternativas para trocar o método atual.",
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp
                )
            }
            MethodRow(
                title = "Acessibilidade",
                status = if (capability.accessibilityServiceEnabled) "Configurada ✓" else "Configurar",
                good = capability.accessibilityServiceEnabled,
                onClick = onAccessibility
            )
            Divider()
            MethodRow(
                title = "Sobreposição normal",
                status = when {
                    capability.canDrawOverlays -> "Disponível ✓"
                    capability.isLowRamDevice -> "Indisponível"
                    else -> "Configurar"
                },
                good = capability.canDrawOverlays,
                enabled = capability.canDrawOverlays || !capability.isLowRamDevice,
                onClick = onSystemOverlay
            )
            Divider()
            MethodRow(
                title = "Notificação",
                status = if (notificationsAllowed) "Disponível ✓" else "Configurar",
                good = notificationsAllowed,
                onClick = onNotification
            )
        }
    }
}

@Composable
private fun MethodRow(
    title: String,
    status: String,
    good: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            color = if (enabled) AppColors.TextPrimary else AppColors.TextSecondary.copy(alpha = 0.5f),
            fontSize = 13.sp
        )
        Text(
            status,
            color = when {
                !enabled -> AppColors.TextSecondary.copy(alpha = 0.5f)
                good -> AppColors.Success
                else -> AppColors.AccentSoft
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text("›", color = AppColors.TextSecondary, fontSize = 18.sp)
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 15.dp),
        color = AppColors.TextSecondary.copy(alpha = 0.08f)
    )
}
