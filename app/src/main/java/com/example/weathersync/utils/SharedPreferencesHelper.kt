package com.example.weathersync.utils

import android.content.Context
import androidx.core.content.edit

object SharedPreferencesHelper {
    private const val PREFS_NAME = "location_prefs"
    private const val KEY_LATITUDE = "latitude"
    private const val KEY_LONGITUDE = "longitude"

    fun saveLocation(context: Context, latitude: Double, longitude: Double) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putFloat(KEY_LATITUDE, latitude.toFloat())
            putFloat(KEY_LONGITUDE, longitude.toFloat())
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
}