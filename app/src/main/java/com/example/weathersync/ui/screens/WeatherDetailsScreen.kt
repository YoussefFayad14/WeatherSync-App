package com.example.weathersync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weathersync.R
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.utils.DrawableUtils
import com.example.weathersync.utils.WeatherUtils

@Composable
fun WeatherDetailsScreen(
    navController: NavHostController,
    weatherEntity: WeatherEntity?
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(40.dp).padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = if (Locale.current.language == "ar") Icons.Default.ArrowForward else Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Box(
                modifier = Modifier.weight(0.2f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.weather_details),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }

        weatherEntity?.let {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    DayFeelsLike(
                        weatherCondition = it.description,
                        feelsLikeTemp = WeatherUtils.getFormattedTemperature(it.feelsLike, context),
                        iconCode = DrawableUtils.getWeatherIconDrawable(it.icon),
                        tempUnit = WeatherUtils.getTemperatureUnitSymbol(context),
                        dayLabel = stringResource(R.string.today),
                        dateLabel = WeatherUtils.getFormattedCurrentDay(context)
                    )
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
                item {
                    TemperatureDisplay(
                        temperature = WeatherUtils.getFormattedTemperature(it.temp, context),
                        tempUnit = WeatherUtils.getTemperatureUnitSymbol(context)
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    Text(
                        text = it.address ?: "Unknown Address",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = stringResource(R.string.daily_details),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                item {
                    DailyDetails(
                        pressure = WeatherUtils.convertNumberToLocale(context, it.pressure.toString()),
                        windSpeed = WeatherUtils.getFormattedWindSpeed(it.speed, context),
                        speedUnit = WeatherUtils.getSpeedUnit(context),
                        tempMax = WeatherUtils.getFormattedTemperature(it.tempMax, context),
                        tempMin = WeatherUtils.getFormattedTemperature(it.tempMin, context),
                        tempType = WeatherUtils.getTemperatureUnitSymbol(context),
                        humidity = WeatherUtils.convertNumberToLocale(context, it.humidity.toString()),
                        clouds = WeatherUtils.convertNumberToLocale(context, it.clouds.toString())
                    )
                }
            }
        }
    }
}
