package com.example.weathersync.utils

import androidx.compose.ui.text.intl.Locale

object SettingUtils{
    fun toSharedPreferencesLanguage(value: String): String {
        return when (value) {
            "English", "الإنجليزية" -> "English"
            "Arabic", "العربية" -> "Arabic"
            "Default", "افتراضي" -> "Default"
            else -> value
        }
    }
    fun fromSharedPreferencesLanguage(value: String): String {
        return when (value) {
            "English" -> "English"
            "Arabic" -> "العربية"
            "Default" -> if (Locale.current.language == "ar") "افتراضي" else "Default"
            else -> value
        }
    }
    fun toSharedPreferencesTemp(value: String): String {
        return when (value) {
            "Celsius", "درجة مئوية" -> "Celsius"
            "Kelvin", "كلفن" -> "Kelvin"
            "Fahrenheit", "فهرنهايت" -> "Fahrenheit"
            else -> value
        }
    }
    fun fromSharedPreferencesTemp(value: String): String {
        return when (value) {
            "Celsius" -> if (Locale.current.language == "ar") "درجة مئوية" else "Celsius"
            "Kelvin" -> if (Locale.current.language == "ar") "كلفن" else "Kelvin"
            "Fahrenheit" -> if (Locale.current.language == "ar") "فهرنهايت" else "Fahrenheit"
            else -> value
        }
    }
    fun toSharedPreferencesWind(value: String): String {
        return when (value) {
            "Meter/Sec", "متر/ثانية" -> "Meter_Sec"
            "Mile/Hour", "ميل/ساعة" -> "Mile_Hour"
            else -> value
        }
    }
    fun fromSharedPreferencesWind(value: String): String {
        return when (value) {
            "Meter_Sec" -> if (Locale.current.language == "ar") "متر/ثانية" else "Meter/Sec"
            "Mile_Hour" -> if (Locale.current.language == "ar") "ميل/ساعة" else "Mile/Hour"
            else -> value
        }
    }
    fun toSharedPreferencesLocation(value: String): String {
        return when (value) {
            "Gps", "نظام تحديد المواقع" -> "Gps"
            "Map", "الخريطة" -> "Map"
            else -> value
        }
    }
    fun fromSharedPreferencesLocation(value: String): String {
        return when (value) {
            "Gps" -> if (Locale.current.language == "ar") "نظام تحديد المواقع" else "Gps"
            "Map" -> if (Locale.current.language == "ar") "الخريطة" else "Map"
            else -> value
        }
    }
}


