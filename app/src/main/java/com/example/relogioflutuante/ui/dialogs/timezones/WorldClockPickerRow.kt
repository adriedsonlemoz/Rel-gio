package com.example.relogioflutuante.ui.dialogs.timezones

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.timezones.WorldClockDisplay
import com.example.relogioflutuante.timezones.WorldClockEntry
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun WorldClockPickerRow(
    entry: WorldClockEntry,
    display: WorldClockDisplay,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !selected, onClick = onSelect)
            .padding(vertical = 11.dp, horizontal = 2.dp),
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
                text = "${entry.country} · ${display.utcOffset}",
                color = AppColors.TextSecondary,
                fontSize = 11.sp
            )
            Text(
                text = display.relativeDescription,
                color = AppColors.TextSecondary.copy(alpha = 0.72f),
                fontSize = 10.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = display.time.dropLast(3),
                color = AppColors.AccentSoft,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (selected) "Adicionado" else "Adicionar",
                color = if (selected) AppColors.Success else AppColors.AccentSoft,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
