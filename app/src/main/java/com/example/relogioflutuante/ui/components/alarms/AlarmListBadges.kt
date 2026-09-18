package com.example.relogioflutuante.ui.components.alarms

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
internal fun ZoneBadge() {
    Surface(color = AppColors.Accent.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
        Text(
            "BRASÍLIA",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            color = AppColors.AccentSoft,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
internal fun RepeatDayChip(label: String, enabled: Boolean) {
    Surface(
        color = if (enabled) AppColors.Accent.copy(alpha = 0.13f) else AppColors.SurfaceStrong,
        shape = RoundedCornerShape(7.dp)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            color = if (enabled) AppColors.AccentSoft else AppColors.TextSecondary,
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
