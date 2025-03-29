package com.example.weathersync.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_table",
    primaryKeys = ["lat", "lon"]
)
data class FavoriteEntity(
    val lat: Double,
    val lon: Double,
    val address: String,
    val weatherEntity: WeatherEntity?
)
