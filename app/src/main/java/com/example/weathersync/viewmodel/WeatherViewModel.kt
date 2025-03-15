package com.example.weathersync.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weathersync.utils.LocationProvider

class WeatherViewModel(private val context: Context) : ViewModel() {

    private val locationProvider = LocationProvider(context)
    private val _locationLiveData = MutableLiveData<Pair<Double, Double>?>()
    val locationLiveData: LiveData<Pair<Double, Double>?> = _locationLiveData
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun getLocation(activity: Activity) {
        locationProvider.getUserLocation(
            callback = { latitude, longitude ->
                _locationLiveData.postValue(Pair(latitude, longitude))
            },
            onError = { message ->
                _message.postValue(message)
            },
            activity = activity
        )
    }
}
