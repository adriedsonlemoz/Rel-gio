package com.example.relogioflutuante.ui.components.timezones

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.R
import com.example.relogioflutuante.timezones.WorldClockDisplay
import com.example.relogioflutuante.timezones.WorldClockEntry
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun WorldClockRow(
    entry: WorldClockEntry,
    display: WorldClockDisplay,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.city,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = buildSubtitle(entry, display),
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
            Text(
                text = display.time,
                color = AppColors.AccentSoft,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmallAction(R.drawable.ic_arrow_up, "Mover para cima", canMoveUp, onMoveUp)
            SmallAction(R.drawable.ic_arrow_down, "Mover para baixo", canMoveDown, onMoveDown)
            SmallAction(R.drawable.ic_delete, "Remover fuso", true, onRemove)
        }
    }
}

@Composable
private fun SmallAction(
    icon: Int,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, enabled = enabled, modifier = Modifier.size(32.dp)) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = if (enabled) AppColors.TextSecondary else AppColors.TextSecondary.copy(alpha = 0.25f),
            modifier = Modifier.size(16.dp)
        )
    }
}

private fun buildSubtitle(entry: WorldClockEntry, display: WorldClockDisplay): String {
    val parts = mutableListOf(entry.country, display.utcOffset, display.relativeOffset)
    display.dayRelation?.let(parts::add)
    return parts.joinToString(" · ")
}
