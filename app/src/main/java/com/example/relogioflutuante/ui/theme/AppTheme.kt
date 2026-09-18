package com.example.relogioflutuante.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColors {
    val Background = Color(0xFF09111F)
    val Surface = Color(0xFF111827)
    val SurfaceStrong = Color(0xFF172033)
    val Accent = Color(0xFF2563EB)
    val AccentSoft = Color(0xFF7DD3FC)
    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFFB6C1D3)
    val Success = Color(0xFF4ADE80)
    val Warning = Color(0xFFFBBF24)
}

@Composable
fun FloatingClockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = AppColors.Accent,
            onPrimary = Color.White,
            secondary = AppColors.AccentSoft,
            onSecondary = AppColors.Background,
            tertiary = AppColors.Success,
            background = AppColors.Background,
            onBackground = AppColors.TextPrimary,
            surface = AppColors.Surface,
            onSurface = AppColors.TextPrimary,
            surfaceVariant = AppColors.SurfaceStrong,
            onSurfaceVariant = AppColors.TextSecondary,
            outline = AppColors.TextSecondary.copy(alpha = 0.45f)
        ),
        content = content
    )
}
