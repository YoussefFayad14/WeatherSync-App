package com.example.weathersync.data.remote

import com.example.weathersync.utils.WEATHER_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val instance: WeatherApiService = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApiService::class.java)
}