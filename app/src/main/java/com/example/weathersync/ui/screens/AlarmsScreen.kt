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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weathersync.R
import com.example.weathersync.data.model.Response
import com.example.weathersync.ui.components.AlarmItem
import com.example.weathersync.ui.components.AnimatedSnackBar
import com.example.weathersync.ui.components.TimePickerBottomSheet
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.DeepNavyBlue1
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.utils.AlertsUtils
import com.example.weathersync.utils.DrawableUtils
import com.example.weathersync.viewmodel.AlarmsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ScheduleExactAlarm")
@Composable
fun AlertsScreen(alertViewModel: AlarmsViewModel) {
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()
    val alarms by alertViewModel.alarms.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    var isAlarmScheduled by remember { mutableStateOf(false) }
    var showPastAlarmError by remember { mutableStateOf(false) }

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
                    painter = painterResource(id = DrawableUtils.getWeatherIconDrawable("Clock")),
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
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val composition by rememberLottieComposition(
                                        LottieCompositionSpec.RawRes(DrawableUtils.getAnimationDrawable("no_alarm_set"))
                                    )
                                    val progress by animateLottieCompositionAsState(composition)

                                    LottieAnimation(
                                        composition = composition,
                                        progress = { progress },
                                        modifier = Modifier.size(200.dp)
                                    )

                                    Text(
                                        text = stringResource(R.string.no_alarms_set),
                                        fontSize = 24.sp,
                                        color = if (isDarkMode) Color.White else Color.Black,
                                    )
                                }
                            }
                        }
                    } else {
                        items(alarmList, key = { it.id }) { alarm ->
                            val alarmData =
                                AlertsUtils.convertTimeMillisToDayHourMinute(alarm.timeMillis)
                            AlarmItem(
                                day = AlertsUtils.convertNumber(alarmData.first),
                                time = "${AlertsUtils.convertNumber(alarmData.second)}:${AlertsUtils.convertNumber(alarmData.third)}",
                                onDelete = {
                                    alertViewModel.deleteAlarm(alarm.id)
                                    alertViewModel.deleteScheduledAlarm(context, alarm.timeMillis)
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
                            val errorMessage =
                                (alarms as Response.Failure).error.message ?: "Unknown error"
                            Text(text = "Error: $errorMessage", color = Color.Red)
                        }
                    }
                }
            }
        }
        if (isAlarmScheduled) {
            AnimatedSnackBar(stringResource(R.string.alarm_scheduled),"Success")
            LaunchedEffect(Unit) {
                delay(5000)
                isAlarmScheduled = false
            }
        }
        if (showPastAlarmError) {
            AnimatedSnackBar(stringResource(R.string.cannot_set_an_alarm_in_the_past), "Error")
            LaunchedEffect(showPastAlarmError) {
                delay(5000)
                showPastAlarmError = false
            }
        }
    }

    if (showBottomSheet) {
        TimePickerBottomSheet(
            context = context,
            onDismiss = { showBottomSheet = false },
            onTimeSelected = { hour, minute, day ->
                showBottomSheet = false
                val alarmData = AlertsUtils.convertDayHourMinuteToTimeMillis(day, hour, minute)
                if(!alertViewModel.isPastAlarm(alarmData)) {
                    alertViewModel.insertAlarm(alarmData)
                    isAlarmScheduled = alertViewModel.scheduleAlarm(context, alarmData)
                }else{
                    showPastAlarmError = true
                }
            }
        )
    }
}