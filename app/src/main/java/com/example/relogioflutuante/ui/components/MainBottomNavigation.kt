package com.example.relogioflutuante.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun MainBottomNavigation(section: MainSection, onSectionChange: (MainSection) -> Unit) {
    Surface(
        color = AppColors.Surface.copy(alpha = 0.98f),
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
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
    val background by animateColorAsState(
        if (selected) AppColors.Accent.copy(alpha = 0.13f) else AppColors.Surface,
        label = "navBackground"
    )
    val contentColor by animateColorAsState(
        if (selected) AppColors.TextPrimary else AppColors.TextSecondary,
        label = "navContent"
    )
    val indicatorWidth by animateDpAsState(if (selected) 20.dp else 0.dp, label = "navIndicator")

    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(top = 4.dp, bottom = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier.size(if (selected) 21.dp else 19.dp)
            )
        }
        Text(
            item.label,
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
        Box(
            Modifier
                .padding(top = 2.dp)
                .height(2.dp)
                .size(width = indicatorWidth, height = 2.dp)
                .background(AppColors.AccentSoft, CircleShape)
        )
    }
}
