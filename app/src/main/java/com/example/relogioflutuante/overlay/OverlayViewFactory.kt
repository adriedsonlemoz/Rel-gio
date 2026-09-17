package com.example.relogioflutuante.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

data class OverlayViewBinding(
    val root: View,
    val timeText: TextView,
    val statusText: TextView
)

class OverlayViewFactory(private val context: Context) {
    fun create(onClose: () -> Unit): OverlayViewBinding {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(8), dp(8), dp(8))
            background = roundedBackground(Color.argb(238, 17, 24, 39), dp(18).toFloat())
            elevation = dp(8).toFloat()
        }

        val textColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val timeText = TextView(context).apply {
            setTextColor(Color.WHITE)
            textSize = 24f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            includeFontPadding = false
            letterSpacing = 0.04f
        }

        val statusText = TextView(context).apply {
            setTextColor(Color.rgb(148, 163, 184))
            textSize = 10f
            includeFontPadding = false
            visibility = View.GONE
        }

        textColumn.addView(timeText)
        textColumn.addView(statusText)

        val close = TextView(context).apply {
            text = "×"
            setTextColor(Color.rgb(203, 213, 225))
            textSize = 24f
            gravity = Gravity.CENTER
            setPadding(dp(12), 0, dp(6), dp(2))
            setOnClickListener { onClose() }
            contentDescription = "Fechar relógio flutuante"
        }

        root.addView(
            textColumn,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        root.addView(
            close,
            LinearLayout.LayoutParams(dp(44), LinearLayout.LayoutParams.MATCH_PARENT)
        )

        return OverlayViewBinding(root, timeText, statusText)
    }

    private fun roundedBackground(color: Int, radius: Float) = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(color)
        cornerRadius = radius
        setStroke(dp(1), Color.argb(90, 100, 116, 139))
    }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).roundToInt()
}
