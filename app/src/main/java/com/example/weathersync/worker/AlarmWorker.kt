package com.example.weathersync.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathersync.data.local.LocalWeatherDataSource
import com.example.weathersync.data.local.WeatherDatabase
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.remote.RemoteWeatherDataSource
import com.example.weathersync.data.remote.RetrofitClient
import com.example.weathersync.data.repository.WeatherRepositoryImpl
import com.example.weathersync.utils.SharedPreferencesHelper
import com.example.weathersync.utils.SharedPreferencesHelper.KEY_TEMP_UNIT
import kotlinx.coroutines.flow.firstOrNull

class AlarmWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("AlarmWorker", "doWork() started")
        return try {
            val currentTemperatureUnit =
                SharedPreferencesHelper.getSetting(applicationContext, KEY_TEMP_UNIT)

            val repository = WeatherRepositoryImpl(
                RemoteWeatherDataSource(RetrofitClient.instance),
                LocalWeatherDataSource(WeatherDatabase.getInstance(applicationContext).weatherDao())
            )

            val currentWeather = repository.getWeatherList()

            if (currentWeather is Response.Success) {
                val currentWeatherData = currentWeather.data.firstOrNull()
                if (currentWeatherData != null) {
                    val alarmIntent = Intent("com.example.weathersync.ALARM_TRIGGER").apply {
                        setPackage(applicationContext.packageName)
                        putExtra("temperature", currentWeatherData.temp)
                        putExtra("description", currentWeatherData.description)
                        putExtra("humidity", currentWeatherData.humidity)
                        putExtra("currentTemperatureUnit", currentTemperatureUnit)
                    }
                    Log.d("AlarmWorker", "Sending broadcast: $alarmIntent")
                    applicationContext.sendBroadcast(alarmIntent)
                    Log.d("AlarmWorker", "Alarm triggered successfully")
                    return Result.success()
                } else {
                    Log.e("AlarmWorker", "No weather data found")
                }
            } else {
                Log.e("AlarmWorker", "Failed to fetch weather data")
            }
            Result.failure()
        } catch (e: Exception) {
            Log.e("AlarmWorker", "Error in doWork()", e)
            Result.failure()
        }
    }
}
