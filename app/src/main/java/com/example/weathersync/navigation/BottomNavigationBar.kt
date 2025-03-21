package com.example.weathersync.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.weathersync.R
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(
            label = stringResource(R.string.home),
            icon = Icons.Default.Home,
            route = ScreenRoute.HomeScreenRoute.route
        ),
        BottomNavItem(
            label = stringResource(R.string.favorites),
            icon = Icons.Default.Favorite,
            route = ScreenRoute.FavoritesScreenRoute.route
        ),
        BottomNavItem(
            label = stringResource(R.string.alerts),
            icon = Icons.Default.Notifications,
            route = ScreenRoute.NotificationsScreenRoute.route
        ),
        BottomNavItem(
            label = stringResource(R.string.settings),
            icon = Icons.Default.Settings,
            route = ScreenRoute.SettingsScreenRoute.route
        )
    )

    val backgroundColor = if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination?.route

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f))
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                contentColor = Color.White,
            ) {
                items.forEach { item ->
                    val selected = item.route == currentDestination

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                maxLines = 1,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = backgroundColor,
                            selectedTextColor = Color.White,
                            indicatorColor = Color.White,
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White
                        )
                    )
                }
            }
    }
}
