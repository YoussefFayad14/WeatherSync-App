package com.example.weathersync.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.weathersync.R
import com.example.weathersync.utils.LocaleHelper
import com.example.weathersync.utils.LocationProvider
import com.example.weathersync.utils.SettingUtils
import com.example.weathersync.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(context: Context) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow("")
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private val _selectedTempUnit = MutableStateFlow("")
    val selectedTempUnit = _selectedTempUnit.asStateFlow()

    private val _selectedLocationType = MutableStateFlow("")
    val selectedLocationType = _selectedLocationType.asStateFlow()

    private val _selectedWindSpeedUnit = MutableStateFlow("")
    val selectedWindSpeedUnit = _selectedWindSpeedUnit.asStateFlow()

    private val _message = MutableStateFlow<Pair<String, String>>(Pair("", ""))
    val message = _message.asStateFlow()

    init {
        reloadSettings(context)
    }

    fun setLanguage(context: Context, language: String) {
        val storedLanguage = SettingUtils.toSharedPreferencesLanguage(language)
        SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_LANGUAGE, storedLanguage)
        LocaleHelper.setLocale(context, storedLanguage)
        _selectedLanguage.value = SettingUtils.fromSharedPreferencesLanguage(storedLanguage)
    }

    fun setTempUnit(context: Context, unit: String) {
        val storedUnit = SettingUtils.toSharedPreferencesTemp(unit)
        SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_TEMP_UNIT, storedUnit)
        _selectedTempUnit.value = SettingUtils.fromSharedPreferencesTemp(storedUnit)
    }

    fun setLocationType(context: Context, locationType: String) {
        val storeLocationType = SettingUtils.toSharedPreferencesLocation(locationType)
        SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_LOCATION_TYPE, storeLocationType)
        _selectedLocationType.value = SettingUtils.fromSharedPreferencesLocation(storeLocationType)
    }

    fun setWindSpeedUnit(context: Context, unit: String) {
        val storedUnit = SettingUtils.toSharedPreferencesWind(unit)
        SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_WIND_SPEED_UNIT, storedUnit)
        _selectedWindSpeedUnit.value = SettingUtils.fromSharedPreferencesWind(storedUnit)
    }

    @SuppressLint("SuspiciousIndentation")
    fun handleSetLocationType(context: Context, navigate:() -> Unit) {
      val savedLocationType = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LOCATION_TYPE)
        if (savedLocationType == "Map") {
            navigate()
        }else{
            val locationProvider = LocationProvider(context)
            locationProvider.getUserLocation(
                callback = { latitude, longitude ->
                    SharedPreferencesHelper.saveLocation(context, latitude, longitude)
                },
                onError = { errorMessage ->
                    _message.value = Pair(errorMessage,"Error")
                },
                activity = context as Activity
            )
        }

    }

    fun reloadSettings(context: Context) {
        _selectedLanguage.value = SettingUtils.fromSharedPreferencesLanguage(
            SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        )
        _selectedTempUnit.value = SettingUtils.fromSharedPreferencesTemp(
            SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_TEMP_UNIT)
        )
        _selectedLocationType.value = SettingUtils.fromSharedPreferencesLocation(
            SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LOCATION_TYPE)
        )
        _selectedWindSpeedUnit.value = SettingUtils.fromSharedPreferencesWind(
            SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_WIND_SPEED_UNIT)
        )
    }
}
