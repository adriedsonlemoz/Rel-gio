package com.example.relogioflutuante

import android.content.Context
import android.os.SystemClock
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.max

private const val PREFS_NAME = "relogio_flutuante_state"

object ClockState {
    private const val KEY_OFFSET_MS = "clock_offset_ms"
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    fun displayedTimeMillis(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return System.currentTimeMillis() + prefs.getLong(KEY_OFFSET_MS, 0L)
    }

    fun displayedLocalTime(context: Context): LocalTime {
        val offset = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_OFFSET_MS, 0L)
        return LocalTime.now().plusNanos(offset * 1_000_000L)
    }

    fun formattedTime(context: Context): String = displayedLocalTime(context).format(formatter)

    fun setDisplayedTime(context: Context, hour: Int, minute: Int, second: Int) {
        val now = LocalTime.now()
        val target = LocalTime.of(hour, minute, second)
        val offset = Duration.between(now, target).toMillis()
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_OFFSET_MS, offset)
            .apply()
    }

    fun resetToSystemTime(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_OFFSET_MS, 0L)
            .apply()
    }

    fun offsetMillis(context: Context): Long =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_OFFSET_MS, 0L)
}

data class CountdownSnapshot(
    val configuredMillis: Long,
    val remainingMillis: Long,
    val isRunning: Boolean,
    val isFinished: Boolean
)

object CountdownState {
    private const val KEY_CONFIGURED_MS = "countdown_configured_ms"
    private const val KEY_PAUSED_REMAINING_MS = "countdown_paused_remaining_ms"
    private const val KEY_END_ELAPSED_MS = "countdown_end_elapsed_ms"
    private const val KEY_RUNNING = "countdown_running"
    private const val KEY_FINISHED = "countdown_finished"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setDuration(context: Context, hours: Int, minutes: Int, seconds: Int) {
        val total = ((hours * 3600L) + (minutes * 60L) + seconds) * 1000L
        prefs(context).edit()
            .putLong(KEY_CONFIGURED_MS, total)
            .putLong(KEY_PAUSED_REMAINING_MS, total)
            .putLong(KEY_END_ELAPSED_MS, 0L)
            .putBoolean(KEY_RUNNING, false)
            .putBoolean(KEY_FINISHED, false)
            .apply()
    }

    fun start(context: Context) {
        val p = prefs(context)
        val configured = p.getLong(KEY_CONFIGURED_MS, 0L)
        if (configured <= 0L) return
        p.edit()
            .putLong(KEY_PAUSED_REMAINING_MS, configured)
            .putLong(KEY_END_ELAPSED_MS, SystemClock.elapsedRealtime() + configured)
            .putBoolean(KEY_RUNNING, true)
            .putBoolean(KEY_FINISHED, false)
            .apply()
    }

    fun pause(context: Context) {
        val snapshot = snapshot(context)
        if (!snapshot.isRunning) return
        prefs(context).edit()
            .putLong(KEY_PAUSED_REMAINING_MS, snapshot.remainingMillis)
            .putBoolean(KEY_RUNNING, false)
            .apply()
    }

    fun resume(context: Context) {
        val p = prefs(context)
        val remaining = p.getLong(KEY_PAUSED_REMAINING_MS, 0L)
        if (remaining <= 0L) return
        p.edit()
            .putLong(KEY_END_ELAPSED_MS, SystemClock.elapsedRealtime() + remaining)
            .putBoolean(KEY_RUNNING, true)
            .putBoolean(KEY_FINISHED, false)
            .apply()
    }

    fun reset(context: Context) {
        prefs(context).edit()
            .putLong(KEY_PAUSED_REMAINING_MS, 0L)
            .putLong(KEY_END_ELAPSED_MS, 0L)
            .putBoolean(KEY_RUNNING, false)
            .putBoolean(KEY_FINISHED, false)
            .apply()
    }

    fun snapshot(context: Context): CountdownSnapshot {
        val p = prefs(context)
        val configured = p.getLong(KEY_CONFIGURED_MS, 0L)
        val running = p.getBoolean(KEY_RUNNING, false)
        val finishedStored = p.getBoolean(KEY_FINISHED, false)
        val pausedRemaining = p.getLong(KEY_PAUSED_REMAINING_MS, configured)

        if (!running) {
            return CountdownSnapshot(configured, max(0L, pausedRemaining), false, finishedStored)
        }

        val endElapsed = p.getLong(KEY_END_ELAPSED_MS, 0L)
        val remaining = max(0L, endElapsed - SystemClock.elapsedRealtime())
        if (remaining == 0L) {
            p.edit()
                .putLong(KEY_PAUSED_REMAINING_MS, 0L)
                .putBoolean(KEY_RUNNING, false)
                .putBoolean(KEY_FINISHED, true)
                .apply()
            return CountdownSnapshot(configured, 0L, false, true)
        }

        return CountdownSnapshot(configured, remaining, true, false)
    }

    fun acknowledgeFinished(context: Context) {
        prefs(context).edit().putBoolean(KEY_FINISHED, false).apply()
    }
}

enum class OverlayMode { CLOCK, COUNTDOWN }

object OverlayState {
    private const val KEY_ENABLED = "overlay_enabled"
    private const val KEY_MODE = "overlay_mode"
    private const val KEY_X = "overlay_x"
    private const val KEY_Y = "overlay_y"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun mode(context: Context): OverlayMode = runCatching {
        OverlayMode.valueOf(prefs(context).getString(KEY_MODE, OverlayMode.CLOCK.name)!!)
    }.getOrDefault(OverlayMode.CLOCK)

    fun setMode(context: Context, mode: OverlayMode) {
        prefs(context).edit().putString(KEY_MODE, mode.name).apply()
    }

    fun position(context: Context): Pair<Int, Int> =
        prefs(context).getInt(KEY_X, 24) to prefs(context).getInt(KEY_Y, 120)

    fun savePosition(context: Context, x: Int, y: Int) {
        prefs(context).edit().putInt(KEY_X, x).putInt(KEY_Y, y).apply()
    }
}

fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
