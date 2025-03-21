package com.example.weathersync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.weathersync.navigation.SetupNavHost
import com.example.weathersync.ui.theme.WeatherSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WeatherSyncTheme {
                SetupNavHost()
            }
        }
    }
}
