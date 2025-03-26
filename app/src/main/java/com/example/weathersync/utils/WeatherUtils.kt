package com.example.weathersync.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weathersync.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.*

object WeatherUtils {
    @SuppressLint("DefaultLocale")
    fun getFormattedTemperature(value: Double, context: Context): String {
        val state = SharedPreferencesHelper.getSetting(
            context,
            SharedPreferencesHelper.KEY_TEMP_UNIT,
        )

        val temp = when (state.lowercase().split(" ").take(1).joinToString()) {
            "celsius" -> value - 273.15
            "fahrenheit" -> (value - 273.15) * 9 / 5 + 32
            "kelvin" -> value
            else -> return "Error: Invalid unit ($state)"
        }

        return String.format("%.2f", temp)
    }

    @SuppressLint("DefaultLocale")
    fun getFormattedWindSpeed(value: Double, context: Context): String {
        val state = SharedPreferencesHelper.getSetting(
            context,
            SharedPreferencesHelper.KEY_WIND_SPEED_UNIT,
        )

        val speed = when (state) {
            context.getString(R.string.meter_sec) -> value
            context.getString(R.string.mile_hour) -> value * 2.23694
            else -> return "Error: Invalid unit ($state)"
        }
        return String.format("%.2f", speed)
    }

    fun getTemperatureUnitSymbol(context: Context): String {
        val state = SharedPreferencesHelper.getSetting(
            context,
            SharedPreferencesHelper.KEY_TEMP_UNIT,
        )

        return when (state.lowercase().split(" ").take(1).joinToString()) {
            context.getString(R.string.celsius) -> context.getString(R.string.symbol_celsius)
            context.getString(R.string.fahrenheit) -> context.getString(R.string.symbol_fahrenheit)
            context.getString(R.string.kelvin) -> context.getString(R.string.symbol_kelvin)
            else -> context.getString(R.string.symbol_celsius)
        }
    }

    fun getSpeedUnit(context: Context): String {
        val state = SharedPreferencesHelper.getSetting(
            context,
            SharedPreferencesHelper.KEY_WIND_SPEED_UNIT,
        )
        return when (state) {
            context.getString(R.string.meter_sec) -> context.getString(R.string.m_s)
            context.getString(R.string.mile_hour) -> context.getString(R.string.m_h)
            else -> context.getString(R.string.m_s)
        }
    }


    fun getFormattedCurrentDay(context: Context): String {
        val languageCode = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        val locale = Locale(languageCode)
        val dateFormat = SimpleDateFormat("E, dd MMM", locale)

        return dateFormat.format(Date())
    }

    fun getFormattedTime(context: Context): String {
        val languageCode = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        val locale = Locale(languageCode)
        val timeFormat = SimpleDateFormat("hh:mm a", locale)

        return timeFormat.format(Date())
    }


    fun getFormattedDate(context: Context): String {
        val languageCode = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        val locale = Locale(languageCode)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", locale)

        return dateFormat.format(Date())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedTimeFromTimestamp(context: Context, unixTimestamp: Long): String {
        val languageCode = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        val locale = Locale(languageCode)
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", locale)

        return Instant.ofEpochSecond(unixTimestamp)
            .atZone(ZoneId.of("UTC"))
            .format(formatter)
    }


    fun getFormattedDateFromTimestamp(context: Context, unixTime: Long?): String {
        if (unixTime == null) return "Unknown"

        val languageSetting = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        val languageCode = when (languageSetting.lowercase(Locale.ROOT)) {
            "arabic" -> "ar"
            "english" -> "en"
            else -> "en"
        }

        return try {
            val locale = Locale(languageCode)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", locale)
            dateFormat.format(Date(unixTime * 1000L))
        } catch (e: Exception) {
            "Unknown"
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDayFromTimestamp(context: Context, date: String): String {
        val languageSetting = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        val languageCode = when (languageSetting.lowercase()) {
            "arabic" -> "ar"
            "english" -> "en"
            else -> "en"
        }
        val locale = Locale(languageCode)
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale)
            val localDate = LocalDate.parse(date, formatter)
            localDate.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
        } catch (e: DateTimeParseException) {
            "Unknown"
        }
    }
}
