package com.example.weathersync.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey
    val id: Int,
    val coordLat: Double,
    val coordLon: Double,
    var address: String,
    val main: String,
    val description: String,
    val icon: String,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val speed: Double,
    val clouds: Int,
    val timestamp: Long = System.currentTimeMillis()
)
