package com.example.relogioflutuante.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
    OVERLAY("Sobrepor", R.drawable.ic_nav_overlay)
}

@Composable
fun MainBottomNavigation(
    section: MainSection,
    onSectionChange: (MainSection) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MainSection.entries.forEach { item ->
            NavigationItem(
                item = item,
                selected = section == item,
                onClick = { if (section != item) onSectionChange(item) }
            )
        }
    }
}

@Composable
private fun NavigationItem(
    item: MainSection,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) AppColors.Accent.copy(alpha = 0.20f) else AppColors.Surface
    val contentColor = if (selected) AppColors.AccentSoft else AppColors.TextSecondary
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 7.dp),
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
