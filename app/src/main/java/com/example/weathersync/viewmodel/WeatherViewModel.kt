package com.example.weathersync.viewmodel

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersync.R
import com.example.weathersync.data.mapper.*
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.remote.CurrentWeatherResponse
import com.example.weathersync.data.model.remote.ForecastResponse
import com.example.weathersync.data.model.remote.Item
import com.example.weathersync.data.repository.WeatherRepositoryImpl
import com.example.weathersync.utils.LocationProvider
import com.example.weathersync.data.mapper.toCurrentWeatherResponse
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import kotlin.time.Duration.Companion.seconds

class WeatherViewModel(private val context: Context, private val repository: WeatherRepositoryImpl) : ViewModel() {

    private val locationProvider = LocationProvider(context)
    private val _location = MutableStateFlow<Pair<Double?, Double>?>(Pair(0.0, 0.0))
    val location: StateFlow<Pair<Double?, Double>?> = _location.asStateFlow()
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()
    private val _currentWeather = MutableStateFlow<Response<WeatherEntity>>(Response.Loading)
    val currentWeather = _currentWeather.asStateFlow()
    private val _forecastWeather = MutableStateFlow<Response<ForecastResponse>>(Response.Loading)
    val forecastWeather = _forecastWeather.asStateFlow()

    fun loadCurrentWeather() {
        viewModelScope.launch {
            getCurrentLocation(context as Activity)
            val lastLocation = repository.getLastLocation()
            val currentTime = System.currentTimeMillis()
            val threeHoursMillis = 3 * 60 * 60 * 1000

            if (lastLocation != null && location.value != null
                && location.value!!.first != 0.0 && location.value!!.second != 0.0
                && lastLocation.first.toInt() == location.value?.first?.toInt()
                && lastLocation.second.toInt() == location.value?.second?.toInt()
                && currentTime - lastLocation.third < threeHoursMillis
            ) {
                repository.getCachedWeather()
                    .catch { ex -> _message.value = "Error: ${ex.message}" }
                    .collect { response ->
                        if (response is Response.Success) {
                            response.data?.map {
                                _currentWeather.value = Response.Success(it)
                            }
                        }
                    }
            } else {
                if (NetworkHelper.isNetworkAvailable(context)) {
                    repository.getCurrentWeather(location.value!!.first!!, location.value!!.second)
                        .catch { ex -> _message.value = "Error: ${ex.message}" }
                        .collect { response ->
                            if (response is Response.Success) {
                                response.data?.let {
                                    val weatherEntity = it.toWeatherEntity()
                                    weatherEntity.address = getAddressFromLocation()
                                    repository.clearWeather()
                                    repository.saveWeather(weatherEntity)
                                    _currentWeather.value = Response.Success(weatherEntity)
                                }
                            }
                        }
                } else {
                    _message.value = "No Internet Connection"
                    repository.getCachedWeather()
                        .catch { ex -> _message.value = "Error: ${ex.message}" }
                        .collect { response ->
                            if (response is Response.Success) {
                                response.data?.map {
                                    _currentWeather.value = Response.Success(it)
                                }
                            }
                        }
                }
            }
        }
    }

    fun loadForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getForecast(lat, lon)
                .catch { ex -> _message.value = "Error: ${ex.message}"}
                .collect { response -> _forecastWeather.value = response }
        }
    }

    fun convertTemperature(value: Double, from: String, to: String): String {
        val temp = when (from.lowercase() to to.lowercase()) {
            "celsius" to "kelvin" -> value + 273.15
            "celsius" to "fahrenheit" -> (value * 9/5) + 32
            "kelvin" to "celsius" -> value - 273.15
            "kelvin" to "fahrenheit" -> (value - 273.15) * 9/5 + 32
            "fahrenheit" to "celsius" -> (value - 32) * 5/9
            "fahrenheit" to "kelvin" -> (value - 32) * 5/9 + 273.15
            else -> throw IllegalArgumentException("Invalid conversion")
        }
        return String.format("%.2f", temp)
    }

    fun getCurrentDay(): String {
        val dateFormat = SimpleDateFormat("E, dd MMM", Locale.ENGLISH)
        return dateFormat.format(Date())
    }

    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        return timeFormat.format(Date())
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertUnixToTime(unixTimestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
            .withZone(ZoneId.systemDefault())
        return formatter.format(Instant.ofEpochSecond(unixTimestamp))
    }

    fun convertUnixToDate(unixTime: Long?): String {
        return if (unixTime != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(Date(unixTime * 1000))
        } else ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayNameFromDate(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val localDate = LocalDate.parse(date, formatter)
        return localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyForecastForToday(forecastList : List<Item?>?): List<Triple<String, String, Int>> {
        return forecastList
            ?.filter { item ->
                val dataApi = convertUnixToDate(item?.dt?.toLong() ?: 0L)
                dataApi == getCurrentDate()
            }
            ?.take(7)
            ?.mapNotNull { item ->
                val time = convertUnixToTime(item?.dt?.toLong() ?: 0L)
                val temp = convertTemperature(item?.main?.temp ?: 0.0, "kelvin", "celsius").toString()
                val icon = getWeatherIcon(item?.weather?.firstOrNull()?.icon ?: "")
                Triple(time, temp, icon)
            } ?: emptyList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNextDaysForecast(forecastList: List<Item?>?): List<Triple<String, String, Int>> {
        return forecastList
            ?.groupBy { item ->
                convertUnixToDate(item?.dt?.toLong() ?: 0L)
            }
            ?.mapNotNull { (date, items) ->
                val avgTemp = items.mapNotNull { it?.main?.temp }
                    .takeIf { it.isNotEmpty() }
                    ?.average()
                    ?.let { convertTemperature(it, "kelvin", "celsius") }

                val icon = items.mapNotNull { it?.weather?.firstOrNull()?.icon }
                    .groupingBy { it }
                    .eachCount()
                    .maxByOrNull { it.value }?.key

                val weatherIconRes = getWeatherIcon(icon ?: "")

                if (avgTemp != null) Triple(date, avgTemp, weatherIconRes) else null
            }
            ?.take(5) ?: emptyList()
    }

    fun getWeatherIcon(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.drawable.clear_sky_icon
            "01n" -> R.drawable.clear_sky_night_icon
            "02d" -> R.drawable.few_clouds_icon
            "02n" -> R.drawable.few_clouds_night_icon
            "03d" -> R.drawable.cloudy_icon
            "03n" -> R.drawable.cloudy_night_icon
            "04d" -> R.drawable.broken_clouds_icon
            "04n" -> R.drawable.broken_clouds_night_icon
            "09d" -> R.drawable.shower_rain_icon
            "09n" -> R.drawable.shower_rain_night_icon
            "10d" -> R.drawable.rain_icon
            "10n" -> R.drawable.rain_night_icon
            "11d", "11n" -> R.drawable.thunderstorm_icon
            "13d", "13n" -> R.drawable.snow_icon
            "50d" -> R.drawable.mist_icon
            "50n" -> R.drawable.mist_night_icon
            else -> ""
        } as Int
    }

    fun getCurrentLocation(activity: Activity){
        locationProvider.getUserLocation(
            callback = { latitude, longitude ->
                if (latitude == null || longitude == null || (latitude == 0.0 && longitude == 0.0)) {
                    _message.value = "Location not available"
                }else{
                    _location.value = Pair(latitude, longitude)
                }
            },
            onError = { message ->
                _message.value = message
            },
            activity = activity
        )
    }

    fun getAddressFromLocation(): String {
        val geocoder = Geocoder(context, Locale.getDefault())

        return location.value?.let { location ->
            geocoder.getFromLocation(
                location.first?.toDouble() ?: 0.0,
                location.second?.toDouble() ?: 0.0,
                1
            )

        }?.getOrNull(0)?.getAddressLine(0)?.let { address ->
            address
                .split(", ")
                .takeLast(3)
                .let { listOf(it.first(), it[1].split(" ").first(), it.last()) }
                .joinToString(", ")
        } ?: "Unknown Address"
    }

}
