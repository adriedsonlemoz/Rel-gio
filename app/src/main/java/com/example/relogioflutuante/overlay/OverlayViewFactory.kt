package com.example.relogioflutuante.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

data class OverlayViewBinding(
    val root: LinearLayout,
    val grip: TextView,
    val timeText: TextView,
    val statusText: TextView,
    val closeText: TextView
)

class OverlayViewFactory(private val context: Context) {
    fun create(onClose: () -> Unit): OverlayViewBinding {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            elevation = dp(3).toFloat()
        }
        val grip = TextView(context).apply {
            text = "⋮"
            setTextColor(Color.rgb(148, 163, 184))
            textSize = 17f
            gravity = Gravity.CENTER
            includeFontPadding = false
            contentDescription = "Arrastar relógio flutuante"
        }
        val textColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val timeText = TextView(context).apply {
            setTextColor(Color.WHITE)
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            includeFontPadding = false
            letterSpacing = 0.02f
        }
        val statusText = TextView(context).apply {
            setTextColor(Color.rgb(148, 163, 184))
            textSize = 9f
            includeFontPadding = false
            visibility = View.GONE
        }
        val close = TextView(context).apply {
            text = "×"
            setTextColor(Color.rgb(203, 213, 225))
            textSize = 19f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setOnClickListener { onClose() }
            contentDescription = "Fechar relógio flutuante"
        }

        textColumn.addView(timeText)
        textColumn.addView(statusText)
        root.addView(grip)
        root.addView(textColumn)
        root.addView(close)
        return OverlayViewBinding(root, grip, timeText, statusText, close)
    }

    fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).roundToInt()
}
