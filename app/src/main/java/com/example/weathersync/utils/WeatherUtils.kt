package com.example.weathersync.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weathersync.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

object WeatherUtils {
    fun formatWeatherDescriptionForLocale(context: Context, description: String): String {
        val languageSetting = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        val languageCode = when (languageSetting.lowercase(Locale.ROOT)) {
            "arabic" -> "ar"
            "english" -> "en"
            "default" -> context.resources.configuration.locales[0].language
            else -> "en"
        }
        val weatherDescriptions = mapOf(
            "clear sky" to mapOf("en" to "Clear Sky", "ar" to "سماء صافية"),
            "few clouds" to mapOf("en" to "Few Clouds", "ar" to "غيوم قليلة"),
            "scattered clouds" to mapOf("en" to "Scattered Clouds", "ar" to "غيوم متفرقة"),
            "broken clouds" to mapOf("en" to "Broken Clouds", "ar" to "غيوم متكسرة"),
            "overcast clouds" to mapOf("en" to "Overcast Clouds", "ar" to "غيوم ملبدة"),
            "shower rain" to mapOf("en" to "Shower Rain", "ar" to "زخات مطر"),
            "rain" to mapOf("en" to "Rain", "ar" to "مطر"),
            "thunderstorm" to mapOf("en" to "Thunderstorm", "ar" to "عاصفة رعدية"),
            "snow" to mapOf("en" to "Snow", "ar" to "ثلج"),
            "mist" to mapOf("en" to "Mist", "ar" to "ضباب"),
            "haze" to mapOf("en" to "Haze", "ar" to "ضباب خفيف"),
            "fog" to mapOf("en" to "Fog", "ar" to "ضباب كثيف"),
            "sand" to mapOf("en" to "Sandstorm", "ar" to "عاصفة رملية"),
            "dust" to mapOf("en" to "Dust", "ar" to "غبار"),
            "smoke" to mapOf("en" to "Smoke", "ar" to "دخان")
        )

        // Return the translated weather description based on the user's language setting
        return weatherDescriptions[description]?.get(languageCode) ?: description
    }

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

        return String.format("%d", temp.roundToInt())
    }

    @SuppressLint("DefaultLocale")
    fun getFormattedWindSpeed(value: Double, context: Context): String {
        val state = SharedPreferencesHelper.getSetting(
            context,
            SharedPreferencesHelper.KEY_WIND_SPEED_UNIT
        )

        val speed = when (state) {
            "Meter_Sec" -> value
            "Mile_Hour" -> value * 2.23694
            else -> return "Error: Invalid unit ($state)"
        }
        return String.format("%.2f", speed)
    }

    fun getTemperatureUnitSymbol(context: Context): String {
        val localizedContext = LocaleHelper.onAttach(context)
        val state = SharedPreferencesHelper.getSetting(
            localizedContext,
            SharedPreferencesHelper.KEY_TEMP_UNIT
        )

        return when (state) {
            "Celsius" -> localizedContext.getString(R.string.symbol_celsius)
            "Fahrenheit" -> localizedContext.getString(R.string.symbol_fahrenheit)
            "Kelvin" -> localizedContext.getString(R.string.symbol_kelvin)
            else -> localizedContext.getString(R.string.symbol_celsius)
        }
    }

    fun getSpeedUnit(context: Context): String {
        val localizedContext = LocaleHelper.onAttach(context)
        val state = SharedPreferencesHelper.getSetting(
            localizedContext,
            SharedPreferencesHelper.KEY_WIND_SPEED_UNIT,
        )
        return when (state) {
            "Meter_Sec" -> localizedContext.getString(R.string.m_s)
            "Mile_Hour" -> localizedContext.getString(R.string.m_h)
            else -> localizedContext.getString(R.string.m_s)
        }
    }

    fun getFormattedCurrentDay(context: Context): String {
        val languageCode = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        val locale = Locale(languageCode)
        val dateFormat = SimpleDateFormat("E, dd MMM", locale)

        return dateFormat.format(Date())
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
            "default" -> context.resources.configuration.locales[0].language
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

    fun convertNumberToLocale(context: Context,number: String ): String {
        val languageSetting = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        val languageCode = when (languageSetting.lowercase(Locale.ROOT)) {
            "arabic" -> "ar"
            "english" -> "en"
            "default" -> context.resources.configuration.locales[0].language
            else -> "en"
        }
        return if (languageCode == "ar") {
            val easternArabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
            number.map { if (it.isDigit()) easternArabicDigits[it.digitToInt()] else it }.joinToString("")
        } else {
            number
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDayFromTimestamp(context: Context, date: String): String {
        val localizedContext = LocaleHelper.onAttach(context)
        return try {
            val normalizedDate = date.replace(Regex("[٠-٩]")) { match ->
                ARABIC_TO_ENGLISH_DIGITS[match.value] ?: match.value
            }

            val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(normalizedDate)
                ?: return "Unknown"

            if (isToday(parsedDate)) {
                return localizedContext.getString(R.string.today)
            }

            val languageSetting = SharedPreferencesHelper.getSetting(
                context,
                SharedPreferencesHelper.KEY_LANGUAGE
            ).lowercase()

            when (languageSetting) {
                "arabic" -> SimpleDateFormat("EEEE", Locale("ar", "SA")).format(parsedDate)
                "english" -> SimpleDateFormat("EEEE", Locale.ENGLISH).format(parsedDate)
                "default" -> SimpleDateFormat("EEEE", context.resources.configuration.locales[0]).format(parsedDate)
                else -> SimpleDateFormat("EEEE", Locale.ENGLISH).format(parsedDate)
            }
        } catch (e: Exception) {
            Log.e("DateError", "Failed to parse date: ${e.message}")
            "Unknown"
        }
    }

    private fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val inputDate = Calendar.getInstance().apply { time = date }
        return today.get(Calendar.YEAR) == inputDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == inputDate.get(Calendar.DAY_OF_YEAR)
    }

    private val ARABIC_TO_ENGLISH_DIGITS = mapOf(
        "٠" to "0", "١" to "1", "٢" to "2", "٣" to "3", "٤" to "4",
        "٥" to "5", "٦" to "6", "٧" to "7", "٨" to "8", "٩" to "9"
    )

}
