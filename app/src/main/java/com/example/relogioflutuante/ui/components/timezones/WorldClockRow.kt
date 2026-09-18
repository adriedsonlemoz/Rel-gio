package com.example.relogioflutuante.ui.components.timezones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.city,
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${display.utcOffset} · ${display.relativeDescription}",
                color = AppColors.TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
            Text(
                text = buildLocationLine(entry, display),
                color = AppColors.TextSecondary.copy(alpha = 0.72f),
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = display.time,
                color = AppColors.AccentSoft,
                fontSize = 19.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            if (display.dayRelation != null) {
                Text(display.dayRelation, color = AppColors.TextSecondary, fontSize = 10.sp)
            }
        }
        Column {
                IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(34.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert),
                        contentDescription = "Opções de ${entry.city}",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    if (canMoveUp) {
                        DropdownMenuItem(
                            text = { Text("Mover para cima") },
                            onClick = { menuOpen = false; onMoveUp() }
                        )
                    }
                    if (canMoveDown) {
                        DropdownMenuItem(
                            text = { Text("Mover para baixo") },
                            onClick = { menuOpen = false; onMoveDown() }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Remover") },
                        onClick = { menuOpen = false; onRemove() }
                    )
                }
            }
        }
    }

private fun buildLocationLine(entry: WorldClockEntry, display: WorldClockDisplay): String =
    entry.country
