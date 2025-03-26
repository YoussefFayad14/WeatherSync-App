package com.example.weathersync.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import com.example.weathersync.R

@SuppressLint("StaticFieldLeak")
object SharedPreferencesHelper {
    val context: Context? = null
    private const val PREFS_NAME = "app_settings"

    const val KEY_LATITUDE = "latitude"
    const val KEY_LONGITUDE = "longitude"
    const val KEY_LANGUAGE = "language"
    const val KEY_TEMP_UNIT = "temp_unit"
    const val KEY_LOCATION_TYPE = "location_type"
    const val KEY_WIND_SPEED_UNIT = "wind_speed_unit"

    private val DEFAULT_LANGUAGE = context?.getString(R.string.default_language)
    private val DEFAULT_TEMP_UNIT = context?.getString(R.string.temp_unit)
    private val DEFAULT_LOCATION_TYPE = context?.getString(R.string.location)
    private val DEFAULT_WIND_SPEED_UNIT = context?.getString(R.string.wind_speed_unit)
    private const val DEFAULT_LATITUDE = 0.0
    private const val DEFAULT_LONGITUDE = 0.0

    fun saveLocation(context: Context, latitude: Double, longitude: Double) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putFloat(KEY_LATITUDE, latitude.toFloat())
            putFloat(KEY_LONGITUDE, longitude.toFloat())
        }
    }

    fun getLocation(context: Context): Pair<Double, Double> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val latitude = prefs.getFloat(KEY_LATITUDE, DEFAULT_LATITUDE.toFloat()).toDouble()
        val longitude = prefs.getFloat(KEY_LONGITUDE, DEFAULT_LONGITUDE.toFloat()).toDouble()
        return Pair(latitude, longitude)
    }

    fun saveSetting(context: Context, key: String, value: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(key, value)
        }
    }

    fun getSetting(context: Context, key: String): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, getDefaultForKey(key)) ?: ""
    }

    private fun getDefaultForKey(key: String): String {
        return when (key) {
            KEY_LANGUAGE -> DEFAULT_LANGUAGE.toString()
            KEY_TEMP_UNIT -> DEFAULT_TEMP_UNIT.toString()
            KEY_LOCATION_TYPE -> DEFAULT_LOCATION_TYPE.toString()
            KEY_WIND_SPEED_UNIT -> DEFAULT_WIND_SPEED_UNIT.toString()
            else -> ""
        }
    }
}
