package com.example.weathersync.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.weathersync.service.AlarmService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "onReceive() called")
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("temperature", intent?.getDoubleExtra("temperature", 0.0))
            putExtra("description", intent?.getStringExtra("description"))
            putExtra("humidity", intent?.getIntExtra("humidity", 0))
            putExtra("currentTemperatureUnit", intent?.getStringExtra("currentTemperatureUnit"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        }else{
            context.startService(serviceIntent)
        }
    }
}
