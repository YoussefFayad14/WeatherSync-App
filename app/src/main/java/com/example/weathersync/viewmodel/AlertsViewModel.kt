package com.example.weathersync.viewmodel

import android.content.Context
import android.util.Log
import java.util.concurrent.TimeUnit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.AlarmEntity
import com.example.weathersync.data.repository.AlarmRepository
import com.example.weathersync.utils.AlertsUtils.calculateInitialDelay
import com.example.weathersync.worker.AlarmWorker
import com.example.weathersync.worker.DeletePastAlarmsWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertsViewModel(
    context: Context,
    private val repository: AlarmRepository,
) : ViewModel() {
    private val _alarms = MutableStateFlow<Response<List<AlarmEntity>>>(Response.Loading)
    val alarms = _alarms.asStateFlow()

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    init {
        scheduleDailyAlarmCleanup(context)
    }

    fun insertAlarm(timeMillis: Long) {
        viewModelScope.launch {
            repository.insertAlarm(AlarmEntity(timeMillis = timeMillis))
        }
    }

    fun deleteAlarm(alarmId: Int) {
        viewModelScope.launch {
            repository.deleteAlarm(alarmId)
        }
    }

    fun getAllAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms()
                .catch { ex -> _message.value = "Error: ${ex.message}" }
                .collect { response ->
                    if (response is Response.Success) {
                        _alarms.value = Response.Success(response.data ?: emptyList())
                    }
                }
        }
    }

    fun isPastAlarm(alarmData: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        if (alarmData < currentTime) {
            return true
        }
        return false
    }

    fun scheduleAlarm(context: Context, triggerTimeMillis: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val delay = triggerTimeMillis - currentTime
        val uniqueId = "AlarmWorker_$triggerTimeMillis"

        if (delay <= 0) {
            return false
        }

        val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(uniqueId)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueId,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        return true
    }

    fun deleteScheduledAlarm(context: Context, triggerTimeMillis: Long) {
        val workManager = WorkManager.getInstance(context)
        val uniqueTag = "AlarmWorker_$triggerTimeMillis"

        viewModelScope.launch {
            try {
                val workInfos = workManager.getWorkInfosByTag(uniqueTag).get()

                if (workInfos.isEmpty()) {
                    Log.d("AlarmWorker", "No matching work found for tag: $uniqueTag")
                    return@launch
                }

                workInfos.forEach { workInfo ->
                    if (workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
                        workManager.cancelWorkById(workInfo.id)
                        Log.d("AlarmWorker", "Canceled alarm with ID: ${workInfo.id}")
                    }
                }
            } catch (e: Exception) {
                Log.e("AlarmWorker", "Error fetching work info: ${e.message}")
            }
        }
    }

    fun scheduleDailyAlarmCleanup(context: Context) {
        val initialDelay = calculateInitialDelay()
        val workRequest = PeriodicWorkRequestBuilder<DeletePastAlarmsWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .addTag("DailyAlarmCleanupWorker")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyAlarmCleanupWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}