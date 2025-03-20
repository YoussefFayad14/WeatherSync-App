package com.example.weathersync.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_table")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dateTime: String,
    val temperature: Double,
    val description: String,
    val windSpeed: Double
)
