package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun InfoCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Surface.copy(alpha = 0.72f),
        shape = RoundedCornerShape(13.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text("ⓘ", color = AppColors.AccentSoft, fontSize = 13.sp, modifier = Modifier.padding(end = 8.dp))
            Text(
                text = text,
                color = AppColors.TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun StatusLine(label: String, value: String, good: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), color = AppColors.TextSecondary, fontSize = 14.sp)
        Text(
            value,
            color = if (good) AppColors.Success else AppColors.Warning,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
