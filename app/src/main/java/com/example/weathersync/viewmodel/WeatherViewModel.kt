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
import kotlinx.coroutines.Dispatchers
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

    fun loadCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            var currentLocation = SharedPreferencesHelper.getLocation(context)
            var address = ""
            if (currentLocation.first != 0.0 && currentLocation.second != 0.0) {
                _location.value = currentLocation
                address = getAddressFromLocation()
                if (NetworkHelper.isNetworkAvailable(context)){
                    fetchCurrentWeather(location.value!!.first!!, location.value!!.second, address)
                }else{
                    _message.value = context.getString(R.string.no_internet_connection)
                    getCachedCurrentWeather()
                }
            }else{
                getCurrentLocation(context as Activity)
                if (location.value?.first != 0.0 && location.value?.second != 0.0) {
                    address = getAddressFromLocation()
                    if (NetworkHelper.isNetworkAvailable(context)){
                        fetchCurrentWeather(location.value!!.first!!, location.value!!.second, address)
                    }else{
                        _message.value = context.getString(R.string.no_internet_connection)
                        getCachedCurrentWeather()
                    }
                }
            }
        }
    }

    fun loadForecast() {
            viewModelScope.launch(Dispatchers.IO) {
            if (location.value!!.first != 0.0 && location.value!!.second != 0.0) {
                if (NetworkHelper.isNetworkAvailable(context)) {
                    fetchForecast(location.value!!.first!!, location.value!!.second)
                } else {
                    getCachedForecast()
                }
            } else {
               getCachedForecast()
            }
        }
    }

    private fun getCachedCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCachedWeather()
                .catch { ex -> _message.value = "Error: ${ex.message}" }
                .collect { response ->
                    response.map {
                        _currentWeather.value = Response.Success(it)
                    }
                }
        }
    }

    private fun getCachedForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCachedForecast()
                .catch { ex -> _message.value = "Error: ${ex.message}" }
                .collect { response ->
                    response.map {
                        _forecastWeather.value = Response.Success(it)
                    }
                }
        }
    }

    private fun fetchCurrentWeather(lat: Double, lon: Double,address: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentWeather(lat, lon)
                .catch { ex -> _message.value = "Error: ${ex.message}" }
                .collect { response ->
                    if (response is Response.Success) {
                        response.data?.let {
                            val weatherEntity = it.toWeatherEntity().copy(
                                timestamp = System.currentTimeMillis()
                            )
                            weatherEntity.address = address?: "Unknown Address"
                            repository.clearWeather()
                            repository.saveWeather(weatherEntity)
                            _currentWeather.value = Response.Success(weatherEntity)
                        }
                    }
                }
        }
    }

    private fun fetchForecast(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getForecast(lat, lon)
                .catch { ex -> _message.value = "Error: ${ex.message}" }
                .collect { response ->
                    if (response is Response.Success) {
                        response.data?.let {
                            _forecastWeather.value = Response.Success(it.toForecastEntity())
                        }
                    }
                }
        }
    }

    fun getConvertedTemperature(value: Double): String {
        return WeatherUtils.getFormattedTemperature(value, context)
    }

    fun getTemperatureSymbol(): String {
        return WeatherUtils.getTemperatureUnitSymbol(context)
    }

    fun getLocalizedWeatherDescription(value: String): String {
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

    fun covertNumbers(num: String): String {
        return WeatherUtils.convertNumberToLocale(context, num)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayNameFromDate(date: String): String {
        return WeatherUtils.getFormattedDayFromTimestamp(context, date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyForecastForToday(forecastList: List<DailyForecast>?): List<Triple<String, String, Int>> {
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

    fun getCurrentLocation(activity: Activity) {
        locationProvider.getUserLocation(
            callback = { latitude, longitude ->
                if (latitude == null || longitude == null || (latitude == 0.0 && longitude == 0.0)) {
                    _message.value = "Location not available"
                } else {
                    _location.value = Pair(latitude, longitude)
                    SharedPreferencesHelper.saveLocation(context, latitude, longitude)
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
