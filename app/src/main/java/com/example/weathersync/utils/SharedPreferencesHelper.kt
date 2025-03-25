package com.example.weathersync.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit

object SharedPreferencesHelper {
    private const val PREFS_NAME = "app_settings"
    private const val KEY_LATITUDE = "latitude"
    private const val KEY_LONGITUDE = "longitude"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_TEMP_UNIT = "temp_unit"
    private const val KEY_LOCATION_TYPE = "location_type"
    private const val KEY_WIND_SPEED_UNIT = "wind_speed_unit"

    fun saveLocation(context: Context, latitude: Double, longitude: Double) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putFloat(KEY_LATITUDE, latitude.toFloat())
            putFloat(KEY_LONGITUDE, longitude.toFloat())
            apply()
        }
    }

    fun getLocation(context: Context): Pair<Double, Double>? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val latitude = prefs.getFloat(KEY_LATITUDE, Float.NaN)
        val longitude = prefs.getFloat(KEY_LONGITUDE, Float.NaN)
        return if (!latitude.isNaN() && !longitude.isNaN()) {
            Pair(latitude.toDouble(), longitude.toDouble())
        } else {
            null
        }
    }

    fun saveSetting(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit() {
            putString(key, value)
                .apply()
        }
    }

    fun getSetting(context: Context, key: String, defaultValue: String): String {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val value = sharedPreferences.getString(key, defaultValue) ?: defaultValue
        return value
    }
}