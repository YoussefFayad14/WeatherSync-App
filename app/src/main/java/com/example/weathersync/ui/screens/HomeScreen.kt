package com.example.weathersync.ui.screens


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_5
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weathersync.R
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.data.model.remote.ForecastResponse
import com.example.weathersync.ui.components.AnimatedSnackBar
import com.example.weathersync.ui.components.HourlyForecastItem
import com.example.weathersync.ui.components.WeatherDetailItem
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.viewmodel.WeatherViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController, weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) DeepNavyBlue else LightSeaGreen
    val location = weatherViewModel.location.collectAsStateWithLifecycle()
    val currentWeather by weatherViewModel.currentWeather.collectAsStateWithLifecycle()
    val forecastData by weatherViewModel.forecastWeather.collectAsStateWithLifecycle()
    val message by weatherViewModel.message.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing)

    LaunchedEffect(Unit) {
        weatherViewModel.loadCurrentWeather()
    }

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            isRefreshing = true
            weatherViewModel.loadCurrentWeather()
            isRefreshing = false
        },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                backgroundColor = backgroundColor,
                contentColor = if (isDarkMode) LightSeaGreen else DeepNavyBlue
            )
        }
    ) {
        Scaffold(
            containerColor = backgroundColor
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                when (currentWeather) {
                    is Response.Loading -> {
                        CircularProgressIndicator(
                            color = if (isDarkMode) LightSeaGreen else DeepNavyBlue
                        )
                    }
                    is Response.Success -> {
                        val currentWeatherResponse = (currentWeather as? Response.Success)?.data
                        val forecastResponse = (forecastData as? Response.Success)?.data

                        LottieBackground()
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            HomeScreenContent(
                                navController,
                                weatherViewModel,
                                currentWeatherResponse,
                                forecastResponse,
                                message
                            )
                        }
                    }
                    is Response.Failure -> {
                        val errorMessage = (currentWeather as Response.Failure).error.message ?: "Unknown error"

                        Text(
                            text = "Error: $errorMessage",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                }

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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenContent(
    navController: NavHostController,
    viewModel: WeatherViewModel,
    currentWeather: WeatherEntity?,
    forecastData: ForecastResponse?,
    message: String
) {
    AnimatedSnackBar(message)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, top = 0.dp, bottom = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { DayFeelsLike(
            weatherCondition = currentWeather!!.description,
            feelsLikeTemp = viewModel.convertTemperature(currentWeather.feels_like,
                stringResource(R.string.kelvin), stringResource(R.string.celsius)
            ),
            iconCode = viewModel.getWeatherIcon(currentWeather.icon),
            dayLabel = stringResource(R.string.today),
            dateLabel = viewModel.getCurrentDay()
        )
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item { TemperatureDisplay(
                temperature = viewModel.convertTemperature(currentWeather!!.temp,"kelvin","celsius"),
                type = "C"
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { Text(
            text = currentWeather.let { it?.address ?: "Unknown Address" },
            fontSize = 14.sp,
            color = Color.White)
        }
        item { SunsetSunriseRow(viewModel, forecastData?.city?.sunrise, forecastData?.city?.sunset) }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
            currentWeather.let {
                DailyDetails(
                    pressure = it?.pressure?.toString() ?: "N/A",
                    windSpeed = it?.speed?.toString() ?: "N/A",
                    humidity = it?.humidity?.toString() ?: "N/A",
                    clouds = it?.clouds?.toString() ?: "N/A"
                )
            }
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
        item { HourlyDetails(viewModel,forecastData) }
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
        item { NextDaysForecast(viewModel, forecastData) }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DayFeelsLike(weatherCondition: String, feelsLikeTemp: String, iconCode: Int, dayLabel: String, dateLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .size(180.dp, 50.dp),
            colors = CardDefaults.cardColors(Color.Transparent)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = weatherCondition,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = iconCode) ,
                        contentDescription = "Weather Icon",
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
                fontSize = 96.sp,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SunsetSunriseRow(viewModel: WeatherViewModel, sunrise: Int?, sunset: Int?) {
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
            Text(
                text = viewModel.convertUnixToTime(sunset?.toLong() ?: 0),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
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
            Text(
                text = viewModel.convertUnixToTime(sunrise?.toLong() ?: 0),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = "AM", color = Color.White, fontSize = 12.sp)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyDetails(viewModel: WeatherViewModel, forecastData: ForecastResponse?) {
    val hourlyList = viewModel.getHourlyForecastForToday(forecastData?.list?.filterNotNull() ?: emptyList())
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(hourlyList.size) { index ->
            val (time, temp, icon) = hourlyList[index]
            HourlyForecastItem(time.toString(), temp, icon)
        }
    }
}

@Composable
fun DailyDetails(pressure: String, windSpeed: String, humidity: String, clouds: String) {
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
                WeatherDetailItem(icon = R.drawable.cloudy_icon, label = "Clouds", value = "$clouds%")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NextDaysForecast(viewModel: WeatherViewModel, forecastData: ForecastResponse?) {
    val nextDaysList = viewModel.getNextDaysForecast(forecastData?.list?.filterNotNull() ?: emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        nextDaysList.forEach { (day, temp, icon) ->
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = viewModel.getDayNameFromDate(day),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = day,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "Weather Icon",
                        modifier = Modifier
                            .size(32.dp)
                            .weight(0.5f)
                    )

                    Text(
                        text = "${temp.toDouble().toInt()}°C",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
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