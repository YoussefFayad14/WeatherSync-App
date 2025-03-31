package com.example.weathersync.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.ui.screens.*
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.viewmodel.AlarmsViewModel
import com.example.weathersync.viewmodel.AlarmsViewModelFactory
import com.example.weathersync.viewmodel.FavoriteViewModel
import com.example.weathersync.viewmodel.FavoriteViewModelFactory
import com.example.weathersync.viewmodel.SearchViewModel
import com.example.weathersync.viewmodel.SearchViewModelFactory
import com.example.weathersync.viewmodel.SettingsViewModel
import com.example.weathersync.viewmodel.SettingsViewModelFactory
import com.example.weathersync.viewmodel.WeatherViewModel
import com.example.weathersync.viewmodel.WeatherViewModelFactory
import com.google.gson.Gson

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost() {
    val navController = rememberNavController()
    val weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(LocalContext.current)
    )
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(LocalContext.current)
    )
    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(LocalContext.current)
    )
    val alarmsViewModel: AlarmsViewModel = viewModel(
        factory = AlarmsViewModelFactory(LocalContext.current)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
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
                HomeScreen(weatherViewModel)
            }
            composable(ScreenRoute.FavoritesScreenRoute.route) {
                FavoritesScreen(navController, favoriteViewModel)
            }
            composable(ScreenRoute.AlertsScreenRoute.route) {
                AlertsScreen(alarmsViewModel)
            }

            composable(
                route = "settings_screen?message={message}",
                arguments = listOf(navArgument("message") { nullable = true; defaultValue = null })
            ) { backStackEntry ->
                val message = backStackEntry.arguments?.getString("message")
                SettingsScreen(navController, settingsViewModel, message)
            }

            composable(
                route = ScreenRoute.MapScreenRoute.route + "?lat={lat}&lon={lon}&isSettingsChanged={isSettingsChanged}",
                arguments = listOf(
                    navArgument("lat") { nullable = true; defaultValue = null },
                    navArgument("lon") { nullable = true; defaultValue = null },
                    navArgument("isSettingsChanged") { type = NavType.BoolType; defaultValue = false }
                )
            ) { backStackEntry ->
                val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
                val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
                val isSettingsChanged = backStackEntry.arguments?.getBoolean("isSettingsChanged") == false
                MapScreen(navController, favoriteViewModel, lat, lon, isSettingsChanged)
            }

            composable(ScreenRoute.SearchScreenRoute.route) {
                SearchScreen(navController, searchViewModel)
            }

            composable(
                route = ScreenRoute.WeatherDetailsScreenRoute.route + "?weatherEntity={weatherEntity}",
                arguments = listOf(navArgument("weatherEntity") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                val jsonWeatherEntity = backStackEntry.arguments?.getString("weatherEntity")
                val weatherEntity = jsonWeatherEntity?.let {
                    Gson().fromJson(it, WeatherEntity::class.java)
                }

                WeatherDetailsScreen(navController, weatherEntity)
            }

        }
    }
}
