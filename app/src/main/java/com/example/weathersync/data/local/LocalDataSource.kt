package com.example.weathersync.data.local

import com.example.weathersync.data.model.local.AlarmEntity
import com.example.weathersync.data.model.local.FavoriteEntity
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    interface ILocalWeatherDataSource {
        suspend fun saveWeather(weather: WeatherEntity)
        suspend fun saveForecast(forecastList: ForecastEntity)
        suspend fun clearWeather()
        suspend fun clearForecast()
        suspend fun getWeatherList(): List<WeatherEntity>
        fun getCachedWeather(): Flow<List<WeatherEntity>>
        fun getCachedForecast(): Flow<List<ForecastEntity>>
    }

    interface ILocalFavoriteWeatherDataSource {
        suspend fun insertFavorite(favorite: FavoriteEntity)
        suspend fun deleteFavorite(lat: Double, lon: Double)
        suspend fun getFavorite(lat: Double, lon: Double): FavoriteEntity?
        fun getAllFavorites(): Flow<List<FavoriteEntity>>
    }

    interface ILocalAlarmDataSource {
        suspend fun insertAlarm(alarm: AlarmEntity)
        suspend fun deleteAlarm(alarmId: Int)
        suspend fun deletePastAlarms(currentTime: Long)
        suspend fun getAlarmById(alarmId: Int): AlarmEntity?
        fun getAllAlarms(): Flow<List<AlarmEntity>>
    }
}