package com.example.relogioflutuante.ui.layout

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenLayoutRulesTest {
    @Test
    fun `classifica larguras e paddings`() {
        assertEquals(ScreenWidthClass.COMPACT, ScreenLayoutRules.widthClass(360))
        assertEquals(16, ScreenLayoutRules.horizontalPaddingDp(360))
        assertEquals(ScreenWidthClass.NORMAL, ScreenLayoutRules.widthClass(600))
        assertEquals(24, ScreenLayoutRules.horizontalPaddingDp(600))
        assertEquals(ScreenWidthClass.WIDE, ScreenLayoutRules.widthClass(800))
        assertEquals(48, ScreenLayoutRules.horizontalPaddingDp(800))
    }

    @Test
    fun `ajusta tamanho do relogio pela largura`() {
        assertEquals(38, ScreenLayoutRules.timeTextSizeSp(320))
        assertEquals(50, ScreenLayoutRules.timeTextSizeSp(360))
        assertEquals(60, ScreenLayoutRules.timeTextSizeSp(700))
    }
}
