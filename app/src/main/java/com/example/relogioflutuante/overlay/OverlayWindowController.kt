package com.example.relogioflutuante.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.example.relogioflutuante.state.OverlayAppearance
import com.example.relogioflutuante.state.OverlayAppearanceState
import com.example.relogioflutuante.state.OverlayPositionState

class OverlayWindowController(
    private val context: Context,
    private val onClose: () -> Unit,
    private val windowType: Int = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
    private val canShow: () -> Boolean = { Settings.canDrawOverlays(context) }
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val factory = OverlayViewFactory(context)
    private val styler = OverlayViewStyler(factory)
    private var binding: OverlayViewBinding? = null
    private var params: WindowManager.LayoutParams? = null
    private var lastRenderedTime: String? = null
    private var lastFinished: Boolean? = null
    private var lastAppearance: OverlayAppearance? = null
    private var lastOrientation: Int? = null

    fun ensureVisible(): Boolean {
        if (binding != null) return true
        if (!canShow()) return false

        val viewBinding = factory.create(onClose)
        val appearance = OverlayAppearanceState.read(context)
        val (savedX, savedY) = OverlayPositionState.position(context)
        val layoutParams = createLayoutParams(savedX, savedY, appearance)
        params = layoutParams
        configureTouch(viewBinding, appearance)
        styler.apply(viewBinding, appearance)

        if (runCatching { windowManager.addView(viewBinding.root, layoutParams) }.isFailure) {
            params = null
            return false
        }

        binding = viewBinding
        lastAppearance = appearance
        lastOrientation = OverlayPositionState.orientation(context)
        viewBinding.root.post { clampToScreen(useSavedPosition = false) }
        return true
    }

    fun renderClock(time: String) {
        applyRuntimePreferences()
        updateTime(time)
        if (lastFinished != false) {
            binding?.statusText?.visibility = View.GONE
            lastFinished = false
        }
    }

    fun renderCountdown(time: String, finished: Boolean) {
        applyRuntimePreferences()
        updateTime(time)
        if (finished != lastFinished) {
            binding?.statusText?.apply {
                if (finished) {
                    text = "TEMPO ESGOTADO"
                    setTextColor(Color.rgb(248, 113, 113))
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }
            lastFinished = finished
        }
    }

    private fun updateTime(time: String) {
        if (time != lastRenderedTime) {
            binding?.timeText?.text = time
            lastRenderedTime = time
        }
    }

    private fun applyRuntimePreferences() {
        val currentBinding = binding ?: return
        val appearance = OverlayAppearanceState.read(context)
        val orientation = OverlayPositionState.orientation(context)
        if (appearance != lastAppearance) {
            styler.apply(currentBinding, appearance)
            configureTouch(currentBinding, appearance)
            updateFlags(appearance)
            lastAppearance = appearance
            currentBinding.root.post { clampToScreen(useSavedPosition = false) }
        }
        if (orientation != lastOrientation) {
            lastOrientation = orientation
            clampToScreen(useSavedPosition = true)
        }
    }

    private fun configureTouch(binding: OverlayViewBinding, appearance: OverlayAppearance) {
        binding.root.setOnTouchListener(
            if (appearance.positionLocked) null
            else OverlayDragTouchListener(context, windowManager) { params }
        )
    }

    private fun updateFlags(appearance: OverlayAppearance) {
        val root = binding?.root ?: return
        val layoutParams = params ?: return
        layoutParams.flags = baseFlags(appearance)
        runCatching { windowManager.updateViewLayout(root, layoutParams) }
    }

    private fun clampToScreen(useSavedPosition: Boolean) {
        val root = binding?.root ?: return
        val layoutParams = params ?: return
        if (useSavedPosition) {
            val saved = OverlayPositionState.position(context)
            layoutParams.x = saved.first
            layoutParams.y = saved.second
        }
        val metrics = context.resources.displayMetrics
        val maxX = (metrics.widthPixels - root.width).coerceAtLeast(0)
        val maxY = (metrics.heightPixels - root.height).coerceAtLeast(0)
        layoutParams.x = layoutParams.x.coerceIn(0, maxX)
        layoutParams.y = layoutParams.y.coerceIn(0, maxY)
        runCatching { windowManager.updateViewLayout(root, layoutParams) }
    }

    private fun createLayoutParams(
        x: Int,
        y: Int,
        appearance: OverlayAppearance
    ) = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        windowType,
        baseFlags(appearance),
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        this.x = x
        this.y = y
    }

    private fun baseFlags(appearance: OverlayAppearance): Int {
        var flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        if (appearance.positionLocked) {
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }
        return flags
    }

    fun hide() {
        binding?.root?.let { runCatching { windowManager.removeView(it) } }
        binding = null
        params = null
        lastRenderedTime = null
        lastFinished = null
        lastAppearance = null
        lastOrientation = null
    }
}
