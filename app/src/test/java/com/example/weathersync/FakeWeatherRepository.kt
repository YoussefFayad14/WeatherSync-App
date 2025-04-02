package com.example.weathersync.data.repository

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.data.model.remote.CurrentWeatherResponse
import com.example.weathersync.data.model.remote.ForecastResponse
import kotlinx.coroutines.flow.*

class FakeWeatherRepository : WeatherRepository {
    private val weatherList = mutableListOf<WeatherEntity>()
    private val forecastList = mutableListOf<ForecastEntity>()
    private val weatherFlow = MutableStateFlow<List<WeatherEntity>>(emptyList())
    private val forecastFlow = MutableStateFlow<List<ForecastEntity>>(emptyList())

    override fun getCurrentWeather(lat: Double, lon: Double): Flow<Response<CurrentWeatherResponse>> {
        return flow { emit(Response.Success(CurrentWeatherResponse(/* Mock data */))) }
    }

    override fun getForecast(lat: Double, lon: Double): Flow<Response<ForecastResponse>> {
        return flow { emit(Response.Success(ForecastResponse(/* Mock data */))) }
    }

    override suspend fun saveWeather(weather: WeatherEntity) {
        weatherList.add(weather)
        weatherFlow.value = weatherList.toList()
    }

    override suspend fun saveForecast(forecastList: ForecastEntity) {
        this.forecastList.add(forecastList)
        forecastFlow.value = this.forecastList.toList()
    }

    override suspend fun getWeatherList(): List<WeatherEntity> {
        return weatherList.toList()
    }

    override fun getCachedWeather(): Flow<List<WeatherEntity>> {
        return weatherFlow.asStateFlow()
    }

    override fun getCachedForecast(): Flow<List<ForecastEntity>> {
        return forecastFlow.asStateFlow()
    }

    override suspend fun clearWeather() {
        weatherList.clear()
        weatherFlow.value = emptyList()
    }

    override suspend fun clearForecast() {
        forecastList.clear()
        forecastFlow.value = emptyList()
    }
}
