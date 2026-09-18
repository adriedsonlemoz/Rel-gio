package com.example.relogioflutuante.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.R
import com.example.relogioflutuante.ui.theme.AppColors

enum class MainSection(val label: String, @DrawableRes val iconRes: Int) {
    CLOCK("Relógio", R.drawable.ic_nav_clock),
    COUNTDOWN("Contagem", R.drawable.ic_nav_timer),
    ALARMS("Alarmes", R.drawable.ic_nav_alarm),
    OVERLAY("Sobrepor", R.drawable.ic_nav_overlay)
}

@Composable
fun MainBottomNavigation(
    section: MainSection,
    onSectionChange: (MainSection) -> Unit
) {
    Surface(color = AppColors.Surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 7.dp)
        ) {
            MainSection.entries.forEach { item ->
                NavigationItem(
                    modifier = Modifier.weight(1f),
                    item = item,
                    selected = section == item,
                    onClick = { if (section != item) onSectionChange(item) }
                )
            }
        }
    }
}

@Composable
private fun NavigationItem(
    modifier: Modifier,
    item: MainSection,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) AppColors.Accent.copy(alpha = 0.18f) else AppColors.Surface
    val contentColor = if (selected) AppColors.AccentSoft else AppColors.TextSecondary
    Column(
        modifier = modifier
            .padding(horizontal = 3.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = item.label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            item.label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
