package com.example.relogioflutuante.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.relogioflutuante.R
import com.example.relogioflutuante.ui.theme.AppColors

enum class MainSection(
    val label: String,
    @DrawableRes val iconRes: Int
) {
    CLOCK("Relógio", R.drawable.ic_nav_clock),
    COUNTDOWN("Contagem", R.drawable.ic_nav_timer),
    OVERLAY("Sobrepor", R.drawable.ic_nav_overlay)
}

@Composable
fun MainBottomNavigation(
    section: MainSection,
    onSectionChange: (MainSection) -> Unit
) {
    NavigationBar(containerColor = AppColors.Surface) {
        MainSection.entries.forEach { item ->
            NavigationBarItem(
                selected = section == item,
                onClick = { onSectionChange(item) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppColors.AccentSoft,
                    selectedTextColor = AppColors.TextPrimary,
                    indicatorColor = AppColors.Accent.copy(alpha = 0.22f),
                    unselectedIconColor = AppColors.TextSecondary,
                    unselectedTextColor = AppColors.TextSecondary
                )
            )
        }
    }
}
