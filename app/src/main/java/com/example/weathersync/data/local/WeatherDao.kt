package com.example.weathersync.data.local

import androidx.room.*
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastEntity)

    @Query("SELECT * FROM weather_table")
    fun getWeather(): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather_table")
    suspend fun getWeatherList(): List<WeatherEntity>

    @Query("SELECT * FROM forecast_table")
    fun getForecast(): Flow<List<ForecastEntity>>

    @Query("DELETE FROM weather_table")
    suspend fun deleteAllWeather()

    @Query("DELETE FROM forecast_table")
    suspend fun deleteAllForecasts()

}
