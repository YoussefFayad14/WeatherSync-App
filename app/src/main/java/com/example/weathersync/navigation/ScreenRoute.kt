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
    object NotificationsScreenRoute : ScreenRoute("notifications_screen")
}
