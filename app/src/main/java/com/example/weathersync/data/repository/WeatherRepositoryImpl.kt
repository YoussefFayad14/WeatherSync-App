package com.example.weathersync.data.repository

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.remote.CurrentWeatherResponse
import com.example.weathersync.data.model.remote.ForecastResponse
import com.example.weathersync.data.remote.RemoteWeatherDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val remoteWeatherDataSource: RemoteWeatherDataSource
): WeatherRepository {

    override fun getCurrentWeather(lat: Double, lon: Double): Flow<Response<CurrentWeatherResponse>> {
        return remoteWeatherDataSource.getWeatherCurrent(lat, lon)
    }
    override fun getForecast(lat: Double, lon: Double): Flow<Response<ForecastResponse>> {
        return remoteWeatherDataSource.getWeatherForecast(lat, lon)
    }
}
