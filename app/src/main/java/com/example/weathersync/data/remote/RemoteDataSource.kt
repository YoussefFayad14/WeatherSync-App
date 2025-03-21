package com.example.weathersync.data.remote

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.remote.CurrentWeatherResponse
import com.example.weathersync.data.model.remote.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun getWeatherForecast (lat: Double, lon: Double): Flow<Response<ForecastResponse>>
    fun getWeatherCurrent (lat: Double, lon: Double): Flow<Response<CurrentWeatherResponse>>
}