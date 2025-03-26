package com.example.weathersync.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.weathersync.utils.LocaleHelper
import com.example.weathersync.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(context: Context) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(
        SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
    )
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private val _selectedTempUnit = MutableStateFlow(
        SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_TEMP_UNIT)
    )
    val selectedTempUnit = _selectedTempUnit.asStateFlow()

    private val _selectedLocation = MutableStateFlow(
        SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LOCATION_TYPE)
    )
    val selectedLocation = _selectedLocation.asStateFlow()

    private val _selectedWindSpeedUnit = MutableStateFlow(
        SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_WIND_SPEED_UNIT)
    )
    val selectedWindSpeedUnit = _selectedWindSpeedUnit.asStateFlow()

    fun setLanguage(context: Context, language: String) {
        _selectedLanguage.value = language
        SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_LANGUAGE, language)
    }

    fun setTempUnit(context: Context, unit: String) {
        _selectedTempUnit.value = unit
        SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_TEMP_UNIT, unit)
    }

    fun setLocation(context:Context, location: String) {
        _selectedLocation.value = location
        SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_LOCATION_TYPE, location)
    }

    fun setWindSpeedUnit(context: Context, unit: String) {
        _selectedWindSpeedUnit.value = unit
        SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_WIND_SPEED_UNIT, unit)
    }
}
