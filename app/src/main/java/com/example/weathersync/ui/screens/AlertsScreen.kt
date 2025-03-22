package com.example.weathersync.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weathersync.R
import com.example.weathersync.ui.components.FavoriteItem
import com.example.weathersync.ui.components.TimePickerBottomSheet
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.DeepNavyBlue1
import com.example.weathersync.ui.theme.LightSeaGreen

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AlertsScreen(navController: NavHostController) {
    var alerts by remember { mutableStateOf(List(20) { "Alert$it" }) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true  },
                containerColor = if (isSystemInDarkTheme()) DeepNavyBlue1 else LightSeaGreen
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add_alarm_white),
                    contentDescription = "Add Alarm Icon",
                    contentScale = ContentScale.Crop
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(alerts, key = { it }) { alert ->
                FavoriteItem(
                    alert,
                    navigateTo = { navController.navigate("map_screen") },
                    onRemove = { alerts = alerts.filterNot { it == alert } },
                    modifier = Modifier.animateItemPlacement(tween(200))
                )
            }
        }
    }
    if (showBottomSheet) {
        TimePickerBottomSheet(
            context = LocalContext.current,
            onDismiss = { showBottomSheet = false },
            onTimeSelected = { hour, minute, day ->
                //scheduleAlarm(context, hour, minute)
            }
        )
    }
}