package com.example.weathersync.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersync.data.mapper.toWeatherEntity
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.FavoriteEntity
import com.example.weathersync.data.model.local.WeatherEntity
import com.example.weathersync.data.repository.FavoriteRepositoryImpl
import com.example.weathersync.data.repository.WeatherRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Locale

class FavoriteViewModel(
    private val context: Context,
    private val repository: FavoriteRepositoryImpl,
    private val weatherRepository: WeatherRepositoryImpl

): ViewModel() {
    private val _favorites = MutableStateFlow<Response<List<FavoriteEntity>>>(Response.Loading)
    val favorites = _favorites.asStateFlow()
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    @SuppressLint("SuspiciousIndentation")
    fun insertFavorite(latitude: Double?, longitude: Double?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (latitude == null || longitude == null) return@launch
            val address = getAddressFromLocation(latitude, longitude)
            var weatherEntity: WeatherEntity? = null
            val weather = weatherRepository.getCurrentWeather(latitude, longitude)
                weather.collect { response ->
                    if (response is Response.Success) {
                       weatherEntity=  response.data.toWeatherEntity().copy(
                           address = address,
                           timestamp = System.currentTimeMillis(),
                        )
                    }
                }
            val favorite = FavoriteEntity(latitude, longitude, address, weatherEntity)
            repository.insertFavorite(favorite)
        }
    }

    fun deleteFavorite(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavorite(latitude, longitude)
        }
    }

    fun getFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavorites()
                .catch { ex -> _message.value = "Error: ${ex.message}" }
                .collect { _favorites.value = Response.Success(it) }
        }
    }

    fun setMessage(message: String) {
        _message.value = message
    }

    fun clearMessage() {
        _message.value = ""
    }

    fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            addresses?.getOrNull(0)?.getAddressLine(0)?.let { address ->
                address.split(", ")
                    .takeLast(3)
                    .let { listOf(it.first(), it[1].split(" ").firstOrNull() ?: "", it.last()) }
                    .joinToString(", ")
            } ?: "Unknown Address"
        } catch (e: Exception) {
            "Unknown Address"
        }
    }

}