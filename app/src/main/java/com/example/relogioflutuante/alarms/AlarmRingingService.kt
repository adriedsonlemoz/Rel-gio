package com.example.relogioflutuante.alarms

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator

class AlarmRingingService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val handler = Handler(Looper.getMainLooper())
    private val autoStop = Runnable {
        stopRinging()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onCreate() {
        super.onCreate()
        AlarmNotificationManager(this).createChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopRinging()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L
        val alarm = AlarmRepository(this).find(alarmId) ?: alarmFromIntent(intent) ?: run {
            stopSelf()
            return START_NOT_STICKY
        }
        startForeground(
            AlarmNotificationManager.SERVICE_NOTIFICATION_ID,
            AlarmNotificationManager(this).buildRinging(alarm)
        )
        startRinging()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopRinging()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startRinging() {
        stopRinging()
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        runCatching {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setWakeMode(this@AlarmRingingService, PowerManager.PARTIAL_WAKE_LOCK)
                setDataSource(this@AlarmRingingService, uri)
                isLooping = true
                prepare()
                start()
            }
        }
        vibrator = getSystemService(Vibrator::class.java)?.also { device ->
            if (device.hasVibrator()) {
                device.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0)
                )
            }
        }
        handler.postDelayed(autoStop, MAX_RING_MILLIS)
    }

    private fun stopRinging() {
        handler.removeCallbacks(autoStop)
        runCatching { mediaPlayer?.stop() }
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
    }

    private fun alarmFromIntent(intent: Intent?): Alarm? {
        if (intent == null) return null
        val hour = intent.getIntExtra(EXTRA_HOUR, -1)
        val minute = intent.getIntExtra(EXTRA_MINUTE, -1)
        if (hour !in 0..23 || minute !in 0..59) return null
        return Alarm(
            id = intent.getLongExtra(EXTRA_ALARM_ID, -1L),
            hour = hour,
            minute = minute,
            label = intent.getStringExtra(EXTRA_LABEL).orEmpty()
        )
    }

    companion object {
        const val ACTION_RING = "com.example.relogioflutuante.action.RING_ALARM"
        const val ACTION_STOP = "com.example.relogioflutuante.action.STOP_ALARM"
        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_HOUR = "alarm_hour"
        const val EXTRA_MINUTE = "alarm_minute"
        const val EXTRA_LABEL = "alarm_label"
        private const val MAX_RING_MILLIS = 5 * 60 * 1_000L
    }
}
