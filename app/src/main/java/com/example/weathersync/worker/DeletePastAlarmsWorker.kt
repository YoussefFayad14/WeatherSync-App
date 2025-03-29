package com.example.weathersync.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathersync.data.local.LocalAlarmDataSource
import com.example.weathersync.data.local.WeatherDatabase
import com.example.weathersync.data.repository.AlarmRepository
import com.example.weathersync.data.repository.AlarmRepositoryImpl

class DeletePastAlarmsWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val currentTime = System.currentTimeMillis()
            val repo: AlarmRepository = AlarmRepositoryImpl(
                LocalAlarmDataSource(WeatherDatabase.getInstance(applicationContext).alarmDao())
            )
            repo.deletePastAlarms(currentTime)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
