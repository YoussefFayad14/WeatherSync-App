package com.example.weathersync.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.RingtoneManager
import android.media.Ringtone
import android.os.*
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weathersync.R
import com.example.weathersync.utils.WeatherUtils
import java.util.*

class AlarmService : Service(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private val channelId = "weather_alarms_channel"
    private var isTtsInitialized = false
    private var ringtone: Ringtone? = null
    private var alarmManager: AlarmManager? = null
    private var message: String? = null
    private var isPlayingSound = false

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()

        tts = TextToSpeech(this, this)

        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmService", "onStartCommand triggered")
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val temperature = intent?.getDoubleExtra("temperature", 0.0)
        val description = intent?.getStringExtra("description")
        val humidity = intent?.getIntExtra("humidity", 0)
        val currentTemperatureUnit = intent?.getStringExtra("currentTemperatureUnit")

        if (temperature != null && description != null && currentTemperatureUnit != null) {
            val temp = WeatherUtils.getFormattedTemperature(temperature, applicationContext)
            message = "Weather is $description,\nTemperature is ${temp} °$currentTemperatureUnit,\nHumidity: $humidity%."

            playNotificationSound()

            if (isTtsInitialized) {
                speakAlarmMessage(message!!)
            } else {
                Log.w("AlarmService", "TTS not initialized yet, waiting...")
            }
        }

        return START_STICKY
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle("Weather Alert")
            .setContentText(message ?: "Weather alert running")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()
    }

    private fun playNotificationSound() {
        if (isPlayingSound) return

        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        ringtone?.play()
        isPlayingSound = true

        Handler(Looper.getMainLooper()).postDelayed({
            ringtone?.stop()
            isPlayingSound = false

            if (!isTtsInitialized && message != null) {
                speakAlarmMessage(message!!)
            }
        }, 200)
    }

    private fun speakAlarmMessage(message: String) {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                if (utteranceId == "ALARM_FINISHED") {
                    stopForegroundService()
                }
            }

            override fun onError(utteranceId: String?) {
                Log.e("AlarmService", "TTS Error for utterance: $utteranceId")
            }
        })

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ALARM_FINISHED")
        }

        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, params, "ALARM_FINISHED")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                return
            }

            isTtsInitialized = true

            message?.let { speakAlarmMessage(it) }
        } else {
            Log.e("AlarmService", "TTS Initialization failed")
        }
    }

    private fun stopForegroundService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle("Weather Alert")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message ?: "Weather alert dismissed"))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(2, notification)
        }

        stopSelf()
    }

    override fun onDestroy() {
        tts?.shutdown()
        ringtone?.stop()
        isPlayingSound = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
