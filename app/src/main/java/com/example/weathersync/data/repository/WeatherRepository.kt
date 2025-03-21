package com.example.weathersync.data.repository

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.data.model.remote.CurrentWeatherResponse
import com.example.weathersync.data.model.remote.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(lat: Double, lon: Double): Flow<Response<CurrentWeatherResponse>>
    fun getForecast(lat: Double, lon: Double): Flow<Response<ForecastResponse>>
    suspend fun saveWeather(weather: WeatherEntity)
    suspend fun saveForecast(forecastList: ForecastEntity)
    suspend fun getLastLocation(): Triple<Double, Double, Long>
    fun getCachedWeather(): Flow<Response<List<WeatherEntity>>>
    fun getCachedForecast(): Flow<Response<List<ForecastEntity>>>
    suspend fun clearWeather()
    suspend fun clearForecast()
}