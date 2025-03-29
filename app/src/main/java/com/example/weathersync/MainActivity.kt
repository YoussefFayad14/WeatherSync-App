package com.example.weathersync

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.weathersync.navigation.SetupNavHost
import com.example.weathersync.receiver.AlarmReceiver
import com.example.weathersync.ui.theme.WeatherSyncTheme
import com.example.weathersync.utils.LocaleHelper

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
    private lateinit var alarmReceiver: AlarmReceiver

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        alarmReceiver = AlarmReceiver()
        registerAlarmReceiver(this)

        setContent {
            WeatherSyncTheme {
                SetupNavHost()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(alarmReceiver)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerAlarmReceiver(context: Context) {
        val intentFilter = android.content.IntentFilter()
        intentFilter.addAction("com.example.weathersync.ALARM_TRIGGER")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(alarmReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(alarmReceiver, intentFilter)
        }
    }

}
