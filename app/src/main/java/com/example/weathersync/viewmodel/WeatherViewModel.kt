package com.example.weathersync.viewmodel

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersync.R
import com.example.weathersync.data.mapper.*
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.DailyForecast
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.repository.WeatherRepositoryImpl
import com.example.weathersync.utils.LocationProvider
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.utils.DrawableUtils
import com.example.weathersync.utils.NetworkHelper
import com.example.weathersync.utils.SharedPreferencesHelper
import com.example.weathersync.utils.WeatherUtils
import com.example.weathersync.utils.WeatherUtils.getTemperatureUnitSymbol
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WeatherViewModel(private val context: Context, private val repository: WeatherRepositoryImpl) : ViewModel() {
    private val locationProvider = LocationProvider(context)
    private val _location = MutableStateFlow<Pair<Double?, Double>?>(Pair(0.0, 0.0))
    val location = _location.asStateFlow()
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()
    private val _currentWeather = MutableStateFlow<Response<WeatherEntity>>(Response.Loading)
    val currentWeather = _currentWeather.asStateFlow()
    private val _forecastWeather = MutableStateFlow<Response<ForecastEntity>>(Response.Loading)
    val forecastWeather = _forecastWeather.asStateFlow()

    fun loadCurrentWeather() { viewModelScope.launch {
            getCurrentLocation(context as Activity)
            val lastLocation = repository.getLastLocation()
            val lastUpdatedWeather = repository.getLastUpdatedWeather()?:0
            val currentTime = System.currentTimeMillis()
            val threeHoursMillis = 3 * 60 * 60 * 1000

            if (lastLocation != null && location.value != null
                && location.value!!.first != 0.0 && location.value!!.second != 0.0
                && lastLocation.first.toInt() == location.value?.first?.toInt()
                && lastLocation.second.toInt() == location.value?.second?.toInt()
                && currentTime - lastUpdatedWeather < threeHoursMillis
            ) {
                repository.getCachedWeather()
                    .catch { ex -> _message.value = "Error: ${ex.message}" }
                    .collect { response ->
                        if (response is Response.Success) {
                            response.data.map {
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
                                    val weatherEntity = it.toWeatherEntity().copy(
                                        timestamp = System.currentTimeMillis()
                                    )
                                    weatherEntity.address = getAddressFromLocation()
                                    repository.clearWeather()
                                    repository.saveWeather(weatherEntity)
                                    _currentWeather.value = Response.Success(weatherEntity)
                                }
                            }
                        }
                } else {
                    _message.value = context.getString(R.string.no_internet_connection)
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
        } }

    fun loadForecast() { viewModelScope.launch {
        val currentTime = System.currentTimeMillis()
        val twentyFourHoursMillis = 24 * 60 * 60 * 1000
        val lastUpdatedTime = repository.getLastUpdatedForecast()?:0

        if (location.value != null
            && location.value!!.first != 0.0
            && location.value!!.second != 0.0
            && currentTime - lastUpdatedTime < twentyFourHoursMillis
            ) {
                repository.getCachedForecast()
                    .catch { ex -> _message.value = "Error: ${ex.message}" }
                    .collect { response ->
                        if (response is Response.Success) {
                            response.data.map {
                                _forecastWeather.value = Response.Success(it)
                            }
                        }
                    }
            } else {
                if (NetworkHelper.isNetworkAvailable(context)) {
                    repository.getForecast(location.value!!.first!!, location.value!!.second)
                        .catch { ex -> _message.value = "Error: ${ex.message}" }
                        .collect { response ->
                            if (response is Response.Success) {
                                response.data.let {
                                    val forecastEntity = it.toForecastEntity().copy(
                                        timestamp = System.currentTimeMillis()
                                    )
                                    repository.clearForecast()
                                    repository.saveForecast(forecastEntity)
                                    _forecastWeather.value = Response.Success(forecastEntity)
                                }
                            }
                        }
                } else {
                    _message.value = context.getString(R.string.no_internet_connection)
                    repository.getCachedForecast()
                        .catch { ex -> _message.value = "Error: ${ex.message}" }
                        .collect { response ->
                            if (response is Response.Success) {
                                response.data?.map {
                                    _forecastWeather.value = Response.Success(it)
                                }
                            }
                        }
                }
            }
        } }

    fun getConvertedTemperature(value: Double): String {
       return WeatherUtils.getFormattedTemperature(value, context)
    }

    fun getTemperatureSymbol(): String {
        return WeatherUtils.getTemperatureUnitSymbol(context)
    }

    fun getLocalizedWeatherDescription(value: String): String{
        return WeatherUtils.formatWeatherDescriptionForLocale(context, value)
    }

    fun getConvertedWindSpeed(value: Double): String {
        return WeatherUtils.getFormattedWindSpeed(value, context)
    }

    fun getSpeedUnit(): String {
        return WeatherUtils.getSpeedUnit(context)
    }

    fun getCurrentDay(): String {
        return WeatherUtils.getFormattedCurrentDay(context)
    }

    fun getCurrentTime(): String {
        return WeatherUtils.getFormattedTime(context)
    }

    fun getCurrentDate(): String {
        return WeatherUtils.getFormattedDate(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertUnixToTime(unixTimestamp: Long): String {
        return WeatherUtils.getFormattedTimeFromTimestamp(context, unixTimestamp)
    }

    fun convertUnixToDate(unixTime: Long?): String {
        return WeatherUtils.getFormattedDateFromTimestamp(context, unixTime)
    }

    fun covertNumbers(num: String): String{
        return WeatherUtils.convertNumberToLocale(context,num)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayNameFromDate(date: String): String {
        return WeatherUtils.getFormattedDayFromTimestamp(context, date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyForecastForToday(forecastList : List<DailyForecast>?): List<Triple<String, String, Int>> {
        return forecastList
            ?.filter { item ->
                val date = convertUnixToDate(item.date)
                date == getCurrentDate()
            }
            ?.mapNotNull { item ->
                val time = item.dateText.split(" ")[1]
                val temp = getConvertedTemperature(item.temp)
                val icon = DrawableUtils.getWeatherIconDrawable(item.icon)
                Triple(time, temp, icon)
            } ?: emptyList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNextDaysForecast(forecastList: List<DailyForecast>?): List<Triple<String, String, Int>> {
        return forecastList
            ?.groupBy { item ->
                convertUnixToDate(item.date)
            }
            ?.mapNotNull { (date, items) ->
                val avgTemp = items.map { it.temp }
                    .takeIf { it.isNotEmpty() }
                    ?.average()
                    ?.let { getConvertedTemperature(it) }

                val icon = items.map { it.icon }
                    .groupingBy { it }
                    .eachCount()
                    .maxByOrNull { it.value }?.key

                val weatherIconRes = DrawableUtils.getWeatherIconDrawable(icon ?: "")

                if (avgTemp != null) Triple(date, avgTemp, weatherIconRes) else null
            }
            ?.take(5) ?: emptyList()
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
        return locationProvider.getAddress(
            context,
            location.value?.first ?: 0.0,
            location.value?.second ?: 0.0
        )
    }

}
