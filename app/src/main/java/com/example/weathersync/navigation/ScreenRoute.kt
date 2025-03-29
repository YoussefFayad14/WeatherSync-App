package com.example.weathersync.navigation

import android.net.Uri
import com.example.weathersync.data.model.local.WeatherEntity
import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute(val route: String) {
    @Serializable
    object SplashScreenRoute : ScreenRoute("splash_screen")

    @Serializable
    object HomeScreenRoute : ScreenRoute("home_screen")

    @Serializable
    object FavoritesScreenRoute : ScreenRoute("favorites_screen")

    @Serializable
    object SettingsScreenRoute : ScreenRoute("settings_screen")

    @Serializable
    object AlertsScreenRoute : ScreenRoute("alerts_screen")

    @Serializable
    object MapScreenRoute : ScreenRoute("map_screen") {
        fun createRoute(lat: Double?, lon: Double?): String {
            return if (lat != null && lon != null) {
                "map_screen?lat=$lat&lon=$lon"
            } else {
                "map_screen"
            }
        }
    }

    @Serializable
    object SearchScreenRoute : ScreenRoute("search_screen")

    @Serializable
    object WeatherDetailsScreenRoute : ScreenRoute("weather_details_screen") {
        fun createRoute(weatherEntity: WeatherEntity?): String {
            return if (weatherEntity == null) {
                "weather_details_screen"
            } else {
                val jsonWeatherEntity = Uri.encode(Gson().toJson(weatherEntity))
                "weather_details_screen?weatherEntity=$jsonWeatherEntity"
            }
        }
    }

}
