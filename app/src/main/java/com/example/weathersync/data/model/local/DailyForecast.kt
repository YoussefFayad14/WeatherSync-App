package com.example.weathersync.data.model.local

data class DailyForecast(
    val date: Long,
    val temp: Double,
    val tempMin: Double,
    val tempMax: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val mainWeather: String,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val clouds: Int,
    val dateText: String
)
