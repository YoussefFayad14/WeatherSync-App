package com.example.weathersync.utils

import com.example.weathersync.R

object DrawableUtils {
    fun getWeatherIconDrawable(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.drawable.clear_sky_icon
            "01n" -> R.drawable.clear_sky_night_icon
            "02d" -> R.drawable.few_clouds_icon
            "02n" -> R.drawable.few_clouds_night_icon
            "03d" -> R.drawable.cloudy_icon
            "03n" -> R.drawable.cloudy_night_icon
            "04d" -> R.drawable.broken_clouds_icon
            "04n" -> R.drawable.broken_clouds_night_icon
            "09d" -> R.drawable.shower_rain_icon
            "09n" -> R.drawable.shower_rain_night_icon
            "10d" -> R.drawable.rain_icon
            "10n" -> R.drawable.rain_night_icon
            "11d", "11n" -> R.drawable.thunderstorm_icon
            "13d", "13n" -> R.drawable.snow_icon
            "50d" -> R.drawable.mist_icon
            "50n" -> R.drawable.mist_night_icon
            "Pressure" -> R.drawable.pressure_icon
            "Humidity" -> R.drawable.humidity_icon
            "Wind Speed" -> R.drawable.wind_speed_icon
            "Clouds" -> R.drawable.cloudy_icon
            "Temp Max" -> R.drawable.temp_max_icon
            "Temp Min" -> R.drawable.temp_min_icon
            else -> R.drawable.ic_launcher_foreground
        }
    }

    fun getWeatherBackgroundDrawable(state: String): Int {
        return when (state) {
            "snow" -> R.raw.snow_animation
            "rain" -> R.raw.rain_animation
            else -> R.raw.clear_sky_animation
        }
    }
}
