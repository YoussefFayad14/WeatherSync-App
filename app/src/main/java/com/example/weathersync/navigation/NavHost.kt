package com.example.weathersync.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.weathersync.ui.screens.*
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen

@Composable
fun SetupNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if (navController.currentBackStackEntryAsState().value?.destination?.route != ScreenRoute.SplashScreenRoute.route) {
                val backgroundColor = if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen
                Box(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .background(backgroundColor)
                ) {
                    BottomNavigationBar(navController = navController)
                }
            }
        }
    ) {innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.SplashScreenRoute.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ScreenRoute.SplashScreenRoute.route) {
                SplashScreen(navController)
            }
            composable(ScreenRoute.HomeScreenRoute.route) {
                HomeScreen(navController)
            }
            composable(ScreenRoute.FavoritesScreenRoute.route) {
                FavoritesScreen()
            }
            composable(ScreenRoute.NotificationsScreenRoute.route) {
                NotificationsScreen()
            }
            composable(ScreenRoute.SettingsScreenRoute.route) {
                SettingsScreen()
            }
        }
    }
}
