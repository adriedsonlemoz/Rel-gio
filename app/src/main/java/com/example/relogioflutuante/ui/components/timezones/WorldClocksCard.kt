package com.example.relogioflutuante.ui.components.timezones

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.timezones.WorldClockEntry
import com.example.relogioflutuante.timezones.WorldClockTime
import com.example.relogioflutuante.ui.theme.AppColors
import java.time.Instant
import java.time.ZoneId

@Composable
fun WorldClocksCard(
    entries: List<WorldClockEntry>,
    instant: Instant,
    localZone: ZoneId,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        border = BorderStroke(1.dp, AppColors.AccentSoft.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Header(onAdd)
            if (entries.isEmpty()) {
                EmptyState()
            } else {
                entries.forEachIndexed { index, entry ->
                    if (index > 0) {
                        HorizontalDivider(color = AppColors.TextSecondary.copy(alpha = 0.10f))
                    }
                    WorldClockRow(
                        entry = entry,
                        display = WorldClockTime.display(ZoneId.of(entry.zoneId), instant, localZone),
                        canMoveUp = index > 0,
                        canMoveDown = index < entries.lastIndex,
                        onMoveUp = { onMoveUp(index) },
                        onMoveDown = { onMoveDown(index) },
                        onRemove = { onRemove(entry.zoneId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "FUSOS HORÁRIOS",
                color = AppColors.AccentSoft,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Text(
                "Horários salvos",
                color = AppColors.TextSecondary,
                fontSize = 11.sp
            )
        }
        TextButton(onClick = onAdd) {
            Text("+ Adicionar")
        }
    }
}

@Composable
private fun EmptyState() {
    Text(
        text = "Adicione cidades para acompanhar outros horários sem sair desta tela.",
        color = AppColors.TextSecondary,
        fontSize = 12.sp,
        lineHeight = 17.sp
    )
}
