package com.example.weathersync.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.weathersync.ui.screens.*
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.viewmodel.WeatherViewModel
import com.example.weathersync.viewmodel.WeatherViewModelFactory

@Composable
fun SetupNavHost() {
    val navController = rememberNavController()
    val weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(LocalContext.current)
    )

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
                HomeScreen(navController, weatherViewModel)
            }
            composable(ScreenRoute.FavoritesScreenRoute.route) {
                FavoritesScreen(navController)
            }
            composable(ScreenRoute.NotificationsScreenRoute.route) {
                NotificationsScreen()
            }
            composable(ScreenRoute.SettingsScreenRoute.route) {
                SettingsScreen()
            }
            composable(ScreenRoute.MapScreenRoute.route) {
                MapScreen(navController)
            }
        }
    }
}
