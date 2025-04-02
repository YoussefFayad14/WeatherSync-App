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
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weathersync.MainActivity
import com.example.weathersync.R
import com.example.weathersync.utils.WeatherUtils
import java.util.*

class AlarmService : Service(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private val channelId = "weather_alarms_channel"
    private val channelName = "Weather Alarms"
    private val notificationId = 1
    private var isTtsInitialized = false
    private var ringtone: Ringtone? = null
    private var alarmManager: AlarmManager? = null
    private var message: String? = null
    private var isPlayingSound = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        tts = TextToSpeech(this, this)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val initialNotification = createNotification("Weather Alert", "Weather alert running")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                notificationId,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        }  else {
            startForeground(notificationId, initialNotification)
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val temperature = intent?.getDoubleExtra("temperature", 0.0)
        val description = intent?.getStringExtra("description")
        val humidity = intent?.getIntExtra("humidity", 0)
        val currentTemperatureUnit = intent?.getStringExtra("currentTemperatureUnit")

        if (temperature != null && description != null && currentTemperatureUnit != null) {
            val temp = WeatherUtils.getFormattedTemperature(temperature, this)
            message = getString(R.string.weather_alert)+
                    getString(R.string.temperature_is, description, temp) + "$currentTemperatureUnit,"+
                    getString(R.string.humidity)+"$humidity%"

            updateNotification(getString(R.string.weather_alert), message ?: getString(R.string.weather_alert_running))

            playNotificationSound()

            if (isTtsInitialized) {
                speakAlarmMessage(message!!)
            } else {
                Log.w("AlarmService", "TTS not initialized yet, waiting...")
            }
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alert notifications"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }



    private fun createNotification(title: String, text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun updateNotification(title: String, text: String) {
        val notification = createNotification(title, text)
        NotificationManagerCompat.from(this).notify(notificationId, notification)
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
            val currentLocale = Locale.getDefault()
            val isArabic = currentLocale.language == "ar"
            val result = tts?.setLanguage(if (isArabic) Locale("ar") else currentLocale)

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
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle("Weather Alert")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message ?: "Weather alert dismissed"))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(pendingIntent)
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