package com.example.weathersync.data.repository

import com.example.weathersync.data.local.LocalWeatherDataSource
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.data.model.remote.CurrentWeatherResponse
import com.example.weathersync.data.model.remote.ForecastResponse
import com.example.weathersync.data.remote.RemoteWeatherDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val remoteWeatherDataSource: RemoteWeatherDataSource,
    private val localWeatherDataSource: LocalWeatherDataSource
): WeatherRepository {

    override fun getCurrentWeather(lat: Double, lon: Double): Flow<Response<CurrentWeatherResponse>> {
        return remoteWeatherDataSource.getWeatherCurrent(lat, lon)
    }

    override fun getForecast(lat: Double, lon: Double): Flow<Response<ForecastResponse>> {
        return remoteWeatherDataSource.getWeatherForecast(lat, lon)
    }

    override suspend fun saveWeather(weather: WeatherEntity) {
        localWeatherDataSource.saveWeather(weather)
    }

    override suspend fun saveForecast(forecastList: ForecastEntity) {
        localWeatherDataSource.saveForecast(forecastList)
    }

    override suspend fun getLastLocation(): Triple<Double, Double, Long> {
        return localWeatherDataSource.getLastLocation()
    }

    override fun getCachedWeather(): Flow<Response<List<WeatherEntity>>> {
        return localWeatherDataSource.getCachedWeather()
    }

    override fun getCachedForecast(): Flow<Response<List<ForecastEntity>>> {
        return localWeatherDataSource.getCachedForecast()
    }
    override suspend fun clearWeather() {
        localWeatherDataSource.clearWeather()
    }
    override suspend fun clearForecast() {
        localWeatherDataSource.clearForecast()
    }

}
