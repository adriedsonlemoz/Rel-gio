package com.example.relogioflutuante.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.relogioflutuante.state.OverlayState

class OverlayWindowController(
    private val context: Context,
    private val onClose: () -> Unit
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var root: View? = null
    private var timeText: TextView? = null
    private var statusText: TextView? = null
    private var params: WindowManager.LayoutParams? = null
    private var lastRenderedTime: String? = null

    fun ensureVisible(): Boolean {
        if (root != null) return true
        if (!Settings.canDrawOverlays(context)) return false

        val binding = OverlayViewFactory(context).create(onClose)
        val (savedX, savedY) = OverlayState.position(context)
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = savedX
            y = savedY
        }

        params = layoutParams
        binding.root.setOnTouchListener(
            OverlayDragTouchListener(context, windowManager) { params }
        )

        val added = runCatching {
            windowManager.addView(binding.root, layoutParams)
        }.isSuccess
        if (!added) {
            params = null
            return false
        }

        root = binding.root
        timeText = binding.timeText
        statusText = binding.statusText
        lastRenderedTime = null
        return true
    }

    fun renderClock(time: String) {
        if (time != lastRenderedTime) {
            timeText?.text = time
            lastRenderedTime = time
        }
        statusText?.visibility = View.GONE
    }

    fun renderCountdown(time: String, finished: Boolean) {
        if (time != lastRenderedTime) {
            timeText?.text = time
            lastRenderedTime = time
        }
        if (finished) {
            statusText?.apply {
                text = "TEMPO ESGOTADO"
                setTextColor(Color.rgb(248, 113, 113))
                visibility = View.VISIBLE
            }
        } else {
            statusText?.visibility = View.GONE
        }
    }

    fun hide() {
        root?.let { view -> runCatching { windowManager.removeView(view) } }
        root = null
        timeText = null
        statusText = null
        params = null
        lastRenderedTime = null
    }
}
