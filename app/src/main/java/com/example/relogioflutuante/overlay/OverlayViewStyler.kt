package com.example.relogioflutuante.overlay

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.LinearLayout
import com.example.relogioflutuante.state.OverlayAppearance
import com.example.relogioflutuante.state.OverlaySize

class OverlayViewStyler(
    private val factory: OverlayViewFactory
) {
    fun apply(binding: OverlayViewBinding, appearance: OverlayAppearance) {
        val metrics = metricsFor(appearance.size)
        binding.root.apply {
            setPadding(
                factory.dp(if (appearance.positionLocked) 8 else 4),
                factory.dp(metrics.verticalPadding),
                factory.dp(if (appearance.positionLocked) 8 else 3),
                factory.dp(metrics.verticalPadding)
            )
            background = background(appearance.opacityPercent, factory.dp(metrics.radius).toFloat())
        }
        binding.timeText.textSize = metrics.textSizeSp
        binding.grip.apply {
            visibility = if (appearance.positionLocked) View.GONE else View.VISIBLE
            layoutParams = LinearLayout.LayoutParams(factory.dp(22), factory.dp(metrics.heightDp))
        }
        binding.closeText.apply {
            visibility = if (appearance.positionLocked) View.GONE else View.VISIBLE
            layoutParams = LinearLayout.LayoutParams(factory.dp(30), factory.dp(metrics.heightDp))
        }
    }

    private fun background(opacityPercent: Int, radius: Float): GradientDrawable {
        val alpha = (255 * opacityPercent.coerceIn(40, 100) / 100f).toInt()
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.argb(alpha, 17, 24, 39))
            cornerRadius = radius
            setStroke(factory.dp(1), Color.argb((alpha * 0.28f).toInt(), 148, 163, 184))
        }
    }

    private fun metricsFor(size: OverlaySize): OverlayMetrics = when (size) {
        OverlaySize.SMALL -> OverlayMetrics(18f, 2, 11, 34)
        OverlaySize.MEDIUM -> OverlayMetrics(22f, 4, 13, 40)
        OverlaySize.LARGE -> OverlayMetrics(28f, 6, 15, 48)
    }
}

private data class OverlayMetrics(
    val textSizeSp: Float,
    val verticalPadding: Int,
    val radius: Int,
    val heightDp: Int
)
