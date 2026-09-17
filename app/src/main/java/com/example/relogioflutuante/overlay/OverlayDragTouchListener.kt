package com.example.relogioflutuante.overlay

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.relogioflutuante.state.OverlayState
import kotlin.math.abs
import kotlin.math.roundToInt

class OverlayDragTouchListener(
    private val context: Context,
    private val windowManager: WindowManager,
    private val paramsProvider: () -> WindowManager.LayoutParams?
) : View.OnTouchListener {
    private var initialX = 0
    private var initialY = 0
    private var touchX = 0f
    private var touchY = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val params = paramsProvider() ?: return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params.x
                initialY = params.y
                touchX = event.rawX
                touchY = event.rawY
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = (event.rawX - touchX).roundToInt()
                val dy = (event.rawY - touchY).roundToInt()
                if (abs(dx) < dp(1) && abs(dy) < dp(1)) return true

                val maxX = (context.resources.displayMetrics.widthPixels - view.width).coerceAtLeast(0)
                val maxY = (context.resources.displayMetrics.heightPixels - view.height).coerceAtLeast(0)
                params.x = (initialX + dx).coerceIn(0, maxX)
                params.y = (initialY + dy).coerceIn(0, maxY)
                windowManager.updateViewLayout(view, params)
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                OverlayState.savePosition(context, params.x, params.y)
                return true
            }
        }
        return false
    }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).roundToInt()
}
