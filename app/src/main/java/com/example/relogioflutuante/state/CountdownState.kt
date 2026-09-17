package com.example.relogioflutuante.state

import android.content.Context
import android.os.SystemClock
import kotlin.math.max

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

    fun setDuration(context: Context, hours: Int, minutes: Int, seconds: Int) {
        val total = ((hours * 3600L) + (minutes * 60L) + seconds) * 1000L
        context.appPreferences().edit()
            .putLong(KEY_CONFIGURED_MS, total)
            .putLong(KEY_PAUSED_REMAINING_MS, total)
            .putLong(KEY_END_ELAPSED_MS, 0L)
            .putBoolean(KEY_RUNNING, false)
            .putBoolean(KEY_FINISHED, false)
            .apply()
    }

    fun start(context: Context) {
        val prefs = context.appPreferences()
        val configured = prefs.getLong(KEY_CONFIGURED_MS, 0L)
        if (configured <= 0L) return
        prefs.edit()
            .putLong(KEY_PAUSED_REMAINING_MS, configured)
            .putLong(KEY_END_ELAPSED_MS, SystemClock.elapsedRealtime() + configured)
            .putBoolean(KEY_RUNNING, true)
            .putBoolean(KEY_FINISHED, false)
            .apply()
    }

    fun pause(context: Context) {
        val snapshot = snapshot(context)
        if (!snapshot.isRunning) return
        context.appPreferences().edit()
            .putLong(KEY_PAUSED_REMAINING_MS, snapshot.remainingMillis)
            .putBoolean(KEY_RUNNING, false)
            .apply()
    }

    fun resume(context: Context) {
        val prefs = context.appPreferences()
        val remaining = prefs.getLong(KEY_PAUSED_REMAINING_MS, 0L)
        if (remaining <= 0L) return
        prefs.edit()
            .putLong(KEY_END_ELAPSED_MS, SystemClock.elapsedRealtime() + remaining)
            .putBoolean(KEY_RUNNING, true)
            .putBoolean(KEY_FINISHED, false)
            .apply()
    }

    fun reset(context: Context) {
        context.appPreferences().edit()
            .putLong(KEY_PAUSED_REMAINING_MS, 0L)
            .putLong(KEY_END_ELAPSED_MS, 0L)
            .putBoolean(KEY_RUNNING, false)
            .putBoolean(KEY_FINISHED, false)
            .apply()
    }

    fun snapshot(context: Context): CountdownSnapshot {
        val prefs = context.appPreferences()
        val configured = prefs.getLong(KEY_CONFIGURED_MS, 0L)
        val running = prefs.getBoolean(KEY_RUNNING, false)
        val finishedStored = prefs.getBoolean(KEY_FINISHED, false)
        val pausedRemaining = prefs.getLong(KEY_PAUSED_REMAINING_MS, configured)

        if (!running) {
            return CountdownSnapshot(configured, max(0L, pausedRemaining), false, finishedStored)
        }

        val endElapsed = prefs.getLong(KEY_END_ELAPSED_MS, 0L)
        val remaining = max(0L, endElapsed - SystemClock.elapsedRealtime())
        if (remaining == 0L) {
            prefs.edit()
                .putLong(KEY_PAUSED_REMAINING_MS, 0L)
                .putBoolean(KEY_RUNNING, false)
                .putBoolean(KEY_FINISHED, true)
                .apply()
            return CountdownSnapshot(configured, 0L, false, true)
        }

        return CountdownSnapshot(configured, remaining, true, false)
    }

    fun acknowledgeFinished(context: Context) {
        context.appPreferences().edit().putBoolean(KEY_FINISHED, false).apply()
    }
}
