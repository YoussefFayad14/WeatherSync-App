package com.example.weathersync.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.weathersync.service.AlarmService
import com.example.weathersync.worker.AlarmWorker

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "onReceive() called")

        if (intent?.action == "com.example.weathersync.ALARM_TRIGGER") {
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra("temperature", intent?.getDoubleExtra("temperature", 0.0))
                putExtra("description", intent?.getStringExtra("description"))
                putExtra("humidity", intent?.getIntExtra("humidity", 0))
                putExtra("currentTemperatureUnit", intent?.getStringExtra("currentTemperatureUnit"))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
        if (intent?.action == "com.example.weathersync.SNOOZE_ALARM") {
            NotificationManagerCompat.from(context).cancel(2)
            val alarmWorkRequest = OneTimeWorkRequest.Builder(AlarmWorker::class.java)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .addTag("alarm_work")
                .build()

            WorkManager.getInstance(context).enqueue(alarmWorkRequest)

            Log.d("AlarmReceiver", "AlarmWorker has been scheduled with a 1-second delay")
        }
    }
}
