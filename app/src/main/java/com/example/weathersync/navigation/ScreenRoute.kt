package com.example.weathersync.navigation

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
}
