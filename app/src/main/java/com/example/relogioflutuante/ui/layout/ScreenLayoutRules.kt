package com.example.relogioflutuante.ui.layout

enum class ScreenWidthClass { COMPACT, NORMAL, WIDE }

object ScreenLayoutRules {
    fun widthClass(widthDp: Int): ScreenWidthClass = when {
        widthDp >= 700 -> ScreenWidthClass.WIDE
        widthDp >= 500 -> ScreenWidthClass.NORMAL
        else -> ScreenWidthClass.COMPACT
    }

    fun horizontalPaddingDp(widthDp: Int): Int = when (widthClass(widthDp)) {
        ScreenWidthClass.COMPACT -> 16
        ScreenWidthClass.NORMAL -> 24
        ScreenWidthClass.WIDE -> 48
    }

    fun timeTextSizeSp(widthDp: Int): Int = when {
        widthDp < 350 -> 38
        widthDp < 500 -> 50
        else -> 60
    }
}
