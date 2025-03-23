package com.example.weathersync.data.local

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.FavoriteEntity
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    interface ILocalWeatherDataSource {
        suspend fun saveWeather(weather: WeatherEntity)
        suspend fun saveForecast(forecastList: ForecastEntity)
        suspend fun getLastLocation(): Triple<Double, Double, Long>
        suspend fun clearWeather()
        suspend fun clearForecast()
        fun getCachedWeather(): Flow<Response<List<WeatherEntity>>>
        fun getCachedForecast(): Flow<Response<List<ForecastEntity>>>
    }

    interface ILocalFavoriteWeatherDataSource {
        suspend fun insertFavorite(favorite: FavoriteEntity)
        suspend fun deleteFavorite(lat: Double, lon: Double)
        suspend fun getFavorite(lat: Double, lon: Double): FavoriteEntity?
        fun getAllFavorites(): Flow<Response<List<FavoriteEntity>>>
    }
}