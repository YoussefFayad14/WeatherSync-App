package com.example.weathersync.utils

import androidx.compose.ui.text.intl.Locale
import java.util.Calendar

object AlertsUtils {

    fun convertTimeMillisToDayHourMinute(timeMillis: Long): Triple<Int, Int, Int> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return Triple(day, hour, minute)
    }

    fun convertDayHourMinuteToTimeMillis(day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun calculateInitialDelay(): Long {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (now >= timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis - now
    }

    fun convertNumber(number: Int): String{
        val languageCode = Locale.current.language
        return if (languageCode == "ar") {
            number.toString()
                .replace('0', '٠')
                .replace('1', '١')
                .replace('2', '٢')
                .replace('3', '٣')
                .replace('4', '٤')
                .replace('5', '٥')
                .replace('6', '٦')
                .replace('7', '٧')
                .replace('8', '٨')
                .replace('9', '٩')
        }else{
            number.toString()
        }
    }
}
