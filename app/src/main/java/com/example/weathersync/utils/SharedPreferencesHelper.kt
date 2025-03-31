package com.example.weathersync.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.example.weathersync.R

@SuppressLint("StaticFieldLeak")
object SharedPreferencesHelper {
    private const val PREFS_NAME = "app_settings"

    const val KEY_LATITUDE = "latitude"
    const val KEY_LONGITUDE = "longitude"
    const val KEY_LANGUAGE = "language"
    const val KEY_TEMP_UNIT = "temp_unit"
    const val KEY_LOCATION_TYPE = "location_type"
    const val KEY_WIND_SPEED_UNIT = "wind_speed_unit"

    private const val DEFAULT_LATITUDE = 0.0
    private const val DEFAULT_LONGITUDE = 0.0
    private const val DEFAULT_LANGUAGE = "Default"
    private const val DEFAULT_TEMP_UNIT = "Celsius"
    private const val DEFAULT_LOCATION_TYPE = "Gps"
    private const val DEFAULT_WIND_SPEED_UNIT = "Meter_Sec"


    fun saveLocation(context: Context, latitude: Double, longitude: Double) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_LATITUDE, latitude.toString())
            putString(KEY_LONGITUDE, longitude.toString())
        }
    }

    fun getLocation(context: Context): Pair<Double, Double> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val latitude = prefs.getString(KEY_LATITUDE, DEFAULT_LATITUDE.toString())?.toDoubleOrNull() ?: DEFAULT_LATITUDE
        val longitude = prefs.getString(KEY_LONGITUDE, DEFAULT_LONGITUDE.toString())?.toDoubleOrNull() ?: DEFAULT_LONGITUDE
        return Pair(latitude, longitude)
    }

    fun saveSetting(context: Context, key: String, value: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(key, value)
        }
        Log.d("SharedPreferencesHelper", "Saved setting: $key=$value")
    }

    fun getSetting(context: Context, key: String): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, getDefaultForKey(key)) ?: getDefaultForKey(key)
    }

    private fun getDefaultForKey(key: String): String {
        return when (key) {
            KEY_LANGUAGE -> DEFAULT_LANGUAGE
            KEY_TEMP_UNIT -> DEFAULT_TEMP_UNIT
            KEY_LOCATION_TYPE -> DEFAULT_LOCATION_TYPE
            KEY_WIND_SPEED_UNIT -> DEFAULT_WIND_SPEED_UNIT
            else -> ""
        }
    }
}
