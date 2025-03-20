package com.example.weathersync.data.mapper

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
        feels_like = this.main?.feelsLike ?: 0.0,
        pressure = this.main?.pressure ?: 0,
        humidity = this.main?.humidity ?: 0,
        speed = this.wind?.speed ?: 0.0,
        clouds = this.clouds?.all ?: 0,
    )
}

fun toCurrentWeatherResponse(entity: WeatherEntity): CurrentWeatherResponse {
    return CurrentWeatherResponse(
        id = entity.id,
        coord = Coord(lat = entity.coordLat, lon = entity.coordLon),
        name = entity.address,
        weather = listOf(
            Weather(
                main = entity.main,
                description = entity.description,
                icon = entity.icon
            )
        ),
        main = Main(
            temp = entity.temp,
            feelsLike = entity.feels_like,
            pressure = entity.pressure,
            humidity = entity.humidity
        ),
        wind = Wind(speed = entity.speed),
        clouds = Clouds(all = entity.clouds),
    )
}