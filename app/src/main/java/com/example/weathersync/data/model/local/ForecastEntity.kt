package com.example.weathersync.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_table")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val coordLat: Double,
    val coordLon: Double,
    val sunrise: Long,
    val sunset: Long,
    val dailyForecasts: List<DailyForecast>
)