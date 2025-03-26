package com.example.weathersync.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weathersync.R
import com.example.weathersync.data.model.Response
import com.example.weathersync.ui.components.AlarmItem
import com.example.weathersync.ui.components.TimePickerBottomSheet
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.DeepNavyBlue1
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.utils.AlertsUtils
import com.example.weathersync.viewmodel.AlertsViewModel

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AlertsScreen(alertViewModel: AlertsViewModel) {
    val alarms by alertViewModel.alarms.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    val isDarkMode = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        alertViewModel.getAllAlarms()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = if (isDarkMode) DeepNavyBlue1 else LightSeaGreen
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
                .background(if (isDarkMode) DeepNavyBlue else LightSeaGreen)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (alarms) {
                is Response.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = if (isDarkMode) LightSeaGreen else DeepNavyBlue)
                        }
                    }
                }

                is Response.Success -> {
                    val alarmList = (alarms as Response.Success).data
                    if (alarmList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No alarms set", color = Color.White)
                            }
                        }
                    } else {
                        items(alarmList, key = { it.id }) { alarm ->
                            val alarmData = AlertsUtils.convertTimeMillisToDayHourMinute(alarm.timeMillis)
                            AlarmItem(
                                day = alarmData.first.toString(),
                                time = "${alarmData.second}:${alarmData.third}",
                                isEnabled = alarm.isEnabled,
                                onToggle = { isEnabled ->
                                    if (isEnabled) {
                                        alertViewModel.enableAlarm(alarm.id)
                                    } else {
                                        alertViewModel.disableAlarm(alarm.id)
                                    }
                                },
                                onDelete = {
                                    alertViewModel.deleteAlarm(alarm.id)
                                },
                                modifier = Modifier.animateItemPlacement(tween(200))
                            )
                        }
                    }
                }

                is Response.Failure -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val errorMessage = (alarms as Response.Failure).error.message ?: "Unknown error"
                            Text(text = "Error: $errorMessage", color = Color.Red)
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        TimePickerBottomSheet(
            context = LocalContext.current,
            onDismiss = { showBottomSheet = false },
            onTimeSelected = { hour, minute, day ->
                showBottomSheet = false
                val alarmData = AlertsUtils.convertDayHourMinuteToTimeMillis(day, hour, minute)
                alertViewModel.insertAlarm(alarmData)
            }
        )
    }
}