package com.example.weathersync.data.local

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.AlarmEntity
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
    interface ILocalAlarmDataSource {
        suspend fun insertAlarm(alarm: AlarmEntity)
        suspend fun deleteAlarm(alarmId: Int)
        suspend fun deletePastAlarms(currentTime: Long)
        suspend fun getNextAlarm(currentTime: Long): AlarmEntity?
        suspend fun getAlarmById(alarmId: Int): AlarmEntity?
        suspend fun disableAlarm(alarmId: Int)
        suspend fun enableAlarm(alarmId: Int)
        fun getAllAlarms(): Flow<Response<List<AlarmEntity>>>
    }
}