package com.example.weathersync.data.mapper

import com.example.weathersync.data.model.local.DailyForecast
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.data.model.remote.*

fun CurrentWeatherResponse.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        id = this.id ?: 0,
        coordLat = this.coord?.lat ?: 0.0,
        coordLon = this.coord?.lon ?: 0.0,
        address = this.name ?: "Unknown",
        main = this.weather?.firstOrNull()?.main ?: "Unknown",
        description = this.weather?.firstOrNull()?.description ?: "No Description",
        icon = this.weather?.firstOrNull()?.icon ?: "",
        temp = this.main?.temp ?: 0.0,
        tempMin = this.main?.tempMin ?: 0.0,
        tempMax = this.main?.tempMax ?: 0.0,
        feelsLike = this.main?.feelsLike ?: 0.0,
        pressure = this.main?.pressure ?: 0,
        humidity = this.main?.humidity ?: 0,
        speed = this.wind?.speed ?: 0.0,
        clouds = this.clouds?.all ?: 0,
    )
}

fun ForecastResponse.toForecastEntity(): ForecastEntity {
    return ForecastEntity(
        coordLat = this.city?.coord?.lat ?: 0.0,
        coordLon = this.city?.coord?.lon ?: 0.0,
        sunrise = this.city?.sunrise?.toLong() ?: 0L,
        sunset = this.city?.sunset?.toLong() ?: 0L,
        dailyForecasts = this.list?.map {
            DailyForecast(
                date = it?.dt?.toLong() ?: 0L,
                temp = it?.main?.temp ?: 0.0,
                tempMin = it?.main?.tempMin ?: 0.0,
                tempMax = it?.main?.tempMax ?: 0.0,
                feelsLike = it?.main?.feelsLike ?: 0.0,
                humidity = it?.main?.humidity ?: 0,
                pressure = it?.main?.pressure ?: 0,
                mainWeather = it?.weather?.firstOrNull()?.main ?: "Unknown",
                description = it?.weather?.firstOrNull()?.description ?: "No Description",
                icon = it?.weather?.firstOrNull()?.icon ?: "",
                windSpeed = it?.wind?.speed ?: 0.0,
                clouds = it?.clouds?.all ?: 0,
                dateText = it?.dtTxt?: "Unknown"
            )
        } ?: emptyList()
    )
}