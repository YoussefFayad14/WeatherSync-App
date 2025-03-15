package com.example.weathersync.ui.screens


import  android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices.PIXEL_5
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.weathersync.R
import com.example.weathersync.ui.components.AnimatedSnackBar
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.viewmodel.WeatherViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController, weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) DeepNavyBlue else LightSeaGreen
    val location = weatherViewModel.locationLiveData.observeAsState()
    val message = weatherViewModel.message.observeAsState()
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(activity) {
        activity?.let { weatherViewModel.getLocation(it) }
        isLoading.value = false
    }

    Scaffold(
        containerColor = backgroundColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
        ) {
            if (!isLoading.value) {
                LottieBackground()
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    HomeScreenContent(navController, message.value.orEmpty())
                }
            } else {
                val progressColor= if (isDarkMode) LightSeaGreen else DeepNavyBlue
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(progressColor)
                )
            }
        }
    }
}

@Composable
fun LottieBackground() {
    val weatherState = remember { mutableStateOf("snow") }
    val lottieRes = when (weatherState.value) {
        "snow" -> R.raw.snow_animation
        "rain" -> R.raw.rain_animation
        else -> R.raw.clear_sky_animation
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val progress by animateLottieCompositionAsState(composition, iterations = Int.MAX_VALUE)

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun HomeScreenContent(navController: NavHostController,message : String) {
    AnimatedSnackBar(message)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, top = 0.dp, bottom = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { DayFeelsLike(
            weatherCondition = "Light Snow",
            feelsLikeTemp = "-10",
            iconRes = R.drawable.snow_icon,
            dayLabel = "Today",
            dateLabel = "Fri, 18 Feb"
        )
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item { TemperatureDisplay(temperature = "-10",type = "C") }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { Text(text = "Norway, Nordland", fontSize = 14.sp, color = Color.White) }
        item { SunsetSunriseRow() }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Daily Details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        item { DailyDetails(
            pressure = "981",
            windSpeed = "1.49",
            humidity = "96",
            uvIndex = "0",
            clouds = "100"
        )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Hourly Details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        item { HourlyDetails() }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Next Days Details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        item { NextDaysForecast() }
    }
}


@Composable
fun DayFeelsLike(weatherCondition: String, feelsLikeTemp: String, iconRes: Int, dayLabel: String, dateLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .size(150.dp, 50.dp),
            colors = CardDefaults.cardColors(Color.Transparent)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = weatherCondition,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Feels Like $feelsLikeTemp°C",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .size(150.dp, 50.dp)
                .align(Alignment.CenterVertically),
            colors = CardDefaults.cardColors(Color.Transparent)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = dayLabel, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = dateLabel, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun TemperatureDisplay(temperature: String,type: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = temperature,
                fontSize = 108.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "°$type",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun SunsetSunriseRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.sunset_icon),
                contentDescription = "Sunset",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "Sunset", color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "06:45", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "PM", color = Color.White, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.sunrise_icon),
                contentDescription = "Sunrise",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "Sunrise", color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "05:30", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "AM", color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun HourlyDetails() {
    val hours = listOf(
        "8PM" to -9, "9PM" to -10, "10PM" to -10,
        "11PM" to -9, "12AM" to -8, "1AM" to -7, "2AM" to -6
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(hours.size) { index ->
            val (time, temp) = hours[index]

            ElevatedCard(
                modifier = Modifier
                    .padding(8.dp)
                    .border(1.dp, Color.White, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f)),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)

            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = time, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.snow_icon),
                        contentDescription = "Snow"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$temp°C", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DailyDetails(pressure: String, windSpeed: String, humidity: String, uvIndex: String, clouds: String) {
    ElevatedCard(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, Color.White, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem(icon = R.drawable.pressure_icon, label = "Pressure", value = "$pressure hPa")
                WeatherDetailItem(icon = R.drawable.wind_speed_icon, label = "Wind Speed", value = "$windSpeed m/s")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem(icon = R.drawable.humidity_icon, label = "Humidity", value = "$humidity%")
                WeatherDetailItem(icon = R.drawable.ultraviolet_icon, label = "Ultraviolet", value = uvIndex)
                WeatherDetailItem(icon = R.drawable.cloudy_icon, label = "Clouds", value = "$clouds%")
            }
        }
    }
}

@Composable
fun WeatherDetailItem(icon: Int, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 14.sp, color = Color.White)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun NextDaysForecast() {
    val forecast = listOf(
        "Today" to -7,
        "Saturday" to -5,
        "Sunday" to -10,
        "Monday" to -11,
        "Tuesday" to -9
    )

    Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
        forecast.forEach { (day, temp) ->
            val weatherIcon = when {
                temp >= 5 -> R.drawable.sunny_icon
                temp in -5..4 -> R.drawable.cloudy_icon
                temp in -10..-6 -> R.drawable.snow_icon
                else -> R.drawable.snowman_icon
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .border(1.dp, Color.White, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = day,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(id = weatherIcon),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${temp}°C",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, device = PIXEL_5)
@Composable
fun HomeScreenPreview() {
    //HomeScreen()
}