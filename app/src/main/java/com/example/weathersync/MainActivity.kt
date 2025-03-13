package com.example.weathersync

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weathersync.ui.screens.*
import com.example.weathersync.ui.theme.WeatherSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        setContent {
            WeatherSyncTheme {
                HomeScreen()
            }
        }
    }
}
