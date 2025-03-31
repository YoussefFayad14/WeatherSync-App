package com.example.weathersync.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathersync.data.local.LocalAlarmDataSource
import com.example.weathersync.data.local.WeatherDatabase
import com.example.weathersync.data.repository.AlarmRepositoryImpl

class AlarmsViewModelFactory (private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmsViewModel::class.java)) {
            return AlarmsViewModel(
                context,
                AlarmRepositoryImpl(
                    LocalAlarmDataSource(WeatherDatabase.getInstance(context).alarmDao())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}