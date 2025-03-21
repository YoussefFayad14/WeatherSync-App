package com.example.weathersync.data.remote

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.remote.CurrentWeatherResponse
import com.example.weathersync.data.model.remote.ForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteWeatherDataSource(private val service: WeatherApiService) : RemoteDataSource {
    override fun getWeatherCurrent(lat: Double, lon: Double): Flow<Response<CurrentWeatherResponse>> = flow {
        try {
            emit(Response.Loading)
            val weather = service.getCurrentWeather(lat, lon)
            emit(Response.Success(weather))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }
    override fun getWeatherForecast(lat: Double, lon: Double): Flow<Response<ForecastResponse>> = flow {
        try {
            emit(Response.Loading)
            val weather = service.getForecast(lat, lon)
            emit(Response.Success(weather))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }
}
