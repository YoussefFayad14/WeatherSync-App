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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_5
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.*
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weathersync.R
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.DailyForecast
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.ui.components.AnimatedSnackBar
import com.example.weathersync.ui.components.HourlyForecastItem
import com.example.weathersync.ui.components.WeatherDetailsItem
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.utils.DrawableUtils
import com.example.weathersync.viewmodel.WeatherViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(weatherViewModel: WeatherViewModel) {
    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) DeepNavyBlue else LightSeaGreen
    val currentWeather by weatherViewModel.currentWeather.collectAsStateWithLifecycle()
    val forecastData by weatherViewModel.forecastWeather.collectAsStateWithLifecycle()
    val message by weatherViewModel.message.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing)
    var showSnackBar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        weatherViewModel.loadCurrentWeather()
        weatherViewModel.loadForecast()
    }

    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            showSnackBar = true
        }
    }

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            isRefreshing = true
            weatherViewModel.loadCurrentWeather()
            weatherViewModel.loadForecast()
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

                        LottieBackground(currentWeatherResponse?.main ?: "")
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            HomeScreenContent(
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
                if (showSnackBar) {
                    AnimatedSnackBar(message)
                    LaunchedEffect(Unit) {
                        delay(3000)
                        showSnackBar = false
                    }
                }
            }
        }
    }
}

@Composable
fun LottieBackground(state: String) {
    val lottieRes = DrawableUtils.getWeatherBackgroundDrawable(state)
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
    viewModel: WeatherViewModel,
    currentWeather: WeatherEntity?,
    forecastData: ForecastEntity?,
    message: String,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, top = 0.dp, bottom = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { DayFeelsLike(
            weatherCondition = viewModel.getLocalizedWeatherDescription(currentWeather!!.description),
            feelsLikeTemp = viewModel.getConvertedTemperature(currentWeather.feelsLike),
            iconCode = DrawableUtils.getWeatherIconDrawable(currentWeather.icon),
            tempUnit = viewModel.getTemperatureSymbol(),
            dayLabel = stringResource(R.string.today),
            dateLabel = viewModel.getCurrentDay()
        )
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item { TemperatureDisplay(
                temperature = viewModel.getConvertedTemperature(currentWeather!!.temp),
                tempUnit = viewModel.getTemperatureSymbol()
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { Text(
            text = currentWeather.let { it?.address ?: "Unknown Address" },
            fontSize = 14.sp,
            color = Color.White)
        }
        item { SunsetSunriseRow(viewModel, forecastData?.sunrise?.toInt() , forecastData?.sunset?.toInt()) }
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
        item { currentWeather.let {
                DailyDetails(
                    pressure = viewModel.covertNumbers(it?.pressure.toString()),
                    windSpeed = viewModel.getConvertedWindSpeed(it?.speed ?: 0.0),
                    speedUnit = viewModel.getSpeedUnit(),
                    tempMax = viewModel.getConvertedTemperature(it?.tempMax ?: 0.0),
                    tempMin = viewModel.getConvertedTemperature(it?.tempMin ?: 0.0),
                    tempType = viewModel.getTemperatureSymbol(),
                    humidity = viewModel.covertNumbers(it?.humidity.toString()),
                    clouds = viewModel.covertNumbers(it?.clouds.toString())
                )
            } }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.hourly_details),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        item { HourlyDetails(viewModel, forecastData?.dailyForecasts) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.next_days_details),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        item { NextDaysForecast(viewModel, forecastData?.dailyForecasts) }
    }
    AnimatedSnackBar(message)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DayFeelsLike(weatherCondition: String, feelsLikeTemp: String, tempUnit :String,iconCode: Int, dayLabel: String, dateLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier
                .size(180.dp, 60.dp)
                .align(Alignment.CenterVertically),
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
                        text = stringResource(R.string.feels_like)+feelsLikeTemp+tempUnit,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .size(150.dp, 60.dp)
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
fun TemperatureDisplay(temperature: String,tempUnit: String) {
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
                text = tempUnit,
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
                contentDescription = stringResource(R.string.sunset),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = stringResource(R.string.sunset), color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = viewModel.convertUnixToTime(sunset?.toLong() ?: 0),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.sunrise_icon),
                contentDescription = stringResource(R.string.sunrise),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = stringResource(R.string.sunrise), color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = viewModel.convertUnixToTime(sunrise?.toLong() ?: 0),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyDetails(viewModel: WeatherViewModel, forecastData: List<DailyForecast>?) {
    val hourlyForecast = viewModel.getHourlyForecastForToday(forecastData)
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(hourlyForecast.size) { index ->
            val (time, temp, icon) = hourlyForecast[index]
            val unitSymbol = viewModel.getTemperatureSymbol()
            HourlyForecastItem(time, temp, unitSymbol, icon)
        }
    }
}

@Composable
fun DailyDetails(
    pressure: String,
    windSpeed: String,
    speedUnit: String,
    tempMax: String,
    tempMin: String,
    tempType: String,
    humidity: String,
    clouds: String
) {
    val pressureUnit = stringResource(R.string.hpa)
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
                WeatherDetailsItem(
                    icon = DrawableUtils.getWeatherIconDrawable(stringResource(R.string.pressure_icon)),
                    label = stringResource(R.string.pressure),
                    value = "$pressure $pressureUnit"
                )
                WeatherDetailsItem(
                    icon = DrawableUtils.getWeatherIconDrawable(stringResource(R.string.temp_max_icon)),
                    label = stringResource(R.string.temp_max),
                    value = "$tempMax $tempType"
                )
                WeatherDetailsItem(
                    icon = DrawableUtils.getWeatherIconDrawable(stringResource(R.string.wind_speed_icon)),
                    label = stringResource(R.string.wind_speed),
                    value = "$windSpeed $speedUnit"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailsItem(
                    icon = DrawableUtils.getWeatherIconDrawable(stringResource(R.string.humidity_icon)),
                    label = stringResource(R.string.humidity),
                    value = "$humidity%"
                )
                WeatherDetailsItem(
                    icon = DrawableUtils.getWeatherIconDrawable(stringResource(R.string.temp_min_icon)),
                    label = stringResource(R.string.temp_min),
                    value = "$tempMin $tempType"
                )
                WeatherDetailsItem(
                    icon = DrawableUtils.getWeatherIconDrawable(stringResource(R.string.clouds_icon)),
                    label = stringResource(R.string.clouds),
                    value = "$clouds%"
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NextDaysForecast(viewModel: WeatherViewModel, forecastData: List<DailyForecast>?) {
    val nextDaysForecast = viewModel.getNextDaysForecast(forecastData)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        nextDaysForecast.forEach { (date, temp, icon) ->
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
                            text = viewModel.getDayNameFromDate(date),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = date,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Image(
                        painter = painterResource(icon),
                        contentDescription = stringResource(R.string.weather_icon),
                        modifier = Modifier
                            .size(32.dp)
                            .weight(0.5f)
                    )

                    Text(
                        text = "${temp}${viewModel.getTemperatureSymbol()}",
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