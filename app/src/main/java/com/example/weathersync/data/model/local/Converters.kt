package com.example.weathersync.data.model.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDailyForecastList(value: List<DailyForecast>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDailyForecastList(value: String): List<DailyForecast> {
        val type = object : TypeToken<List<DailyForecast>>() {}.type
        return gson.fromJson(value, type)
    }
}
