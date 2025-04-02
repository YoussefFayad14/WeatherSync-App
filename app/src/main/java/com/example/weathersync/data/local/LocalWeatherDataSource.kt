package com.example.weathersync.data.local

import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalWeatherDataSource(private val dao: WeatherDao): LocalDataSource.ILocalWeatherDataSource {

    override suspend fun saveWeather(weather: WeatherEntity) {
        dao.insertWeather(weather)
    }

    override fun getCachedWeather(): Flow<List<WeatherEntity>> {
        return dao.getWeather()
    }

    override suspend fun getWeatherList(): List<WeatherEntity> {
        return dao.getWeatherList()
    }

    override suspend fun saveForecast(forecast: ForecastEntity) {
        dao.insertForecast(forecast)
    }

    override fun getCachedForecast(): Flow<List<ForecastEntity>> {
        return dao.getForecast()
    }

    override suspend fun clearWeather() {
        dao.deleteAllWeather()
    }

    override suspend fun clearForecast() {
        dao.deleteAllForecasts()
    }

}
