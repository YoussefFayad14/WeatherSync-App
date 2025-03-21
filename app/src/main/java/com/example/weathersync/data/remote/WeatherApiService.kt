package com.example.weathersync.data.remote

import com.example.weathersync.data.model.remote.CurrentWeatherResponse
import com.example.weathersync.data.model.remote.ForecastResponse
import com.example.weathersync.utils.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = API_KEY,
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = API_KEY,
    ): ForecastResponse
}
