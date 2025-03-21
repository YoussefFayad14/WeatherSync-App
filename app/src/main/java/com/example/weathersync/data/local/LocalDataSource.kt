package com.example.weathersync.data.local

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun saveWeather(weather: WeatherEntity)
    suspend fun saveForecast(forecastList: ForecastEntity)
    suspend fun getLastLocation(): Triple<Double, Double, Long>
    fun getCachedWeather(): Flow<Response<List<WeatherEntity>>>
    fun getCachedForecast(): Flow<Response<List<ForecastEntity>>>
    suspend fun clearWeather()
    suspend fun clearForecast()
}