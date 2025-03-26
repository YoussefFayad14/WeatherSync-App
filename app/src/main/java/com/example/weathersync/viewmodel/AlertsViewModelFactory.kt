package com.example.weathersync.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathersync.data.local.LocalAlarmDataSource
import com.example.weathersync.data.local.LocalFavoriteWeatherDataSource
import com.example.weathersync.data.local.WeatherDatabase
import com.example.weathersync.data.repository.AlarmRepositoryImpl
import com.example.weathersync.data.repository.FavoriteRepositoryImpl

class AlertsViewModelFactory (private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            return AlertsViewModel(
                AlarmRepositoryImpl(
                    LocalAlarmDataSource(WeatherDatabase.getInstance(context).alarmDao())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}