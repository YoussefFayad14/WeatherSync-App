package com.example.weathersync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import com.example.weathersync.data.remote.RemoteWeatherDataSource
import com.example.weathersync.data.remote.RetrofitClient
import com.example.weathersync.data.remote.WeatherApiService
import com.example.weathersync.data.repository.WeatherRepositoryImpl

class WeatherViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(
                context,
                WeatherRepositoryImpl(RemoteWeatherDataSource(RetrofitClient.instance))
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
