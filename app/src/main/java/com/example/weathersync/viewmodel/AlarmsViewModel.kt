package com.example.weathersync.viewmodel

import android.content.Context
import android.util.Log
import java.util.concurrent.TimeUnit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.AlarmEntity
import com.example.weathersync.data.repository.AlarmRepository
import com.example.weathersync.worker.AlarmWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlarmsViewModel(private val repository: AlarmRepository) : ViewModel() {
    private val _alarms = MutableStateFlow<Response<List<AlarmEntity>>>(Response.Loading)
    val alarms = _alarms.asStateFlow()
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    fun insertAlarm(timeMillis: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlarm(AlarmEntity(timeMillis = timeMillis))
        }
    }

    fun deleteAlarm(alarmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlarm(alarmId)
        }
    }

    fun getAllAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllAlarms()
                .catch { ex -> _message.value = "Error: ${ex.message}" }
                .collect { _alarms.value = Response.Success(it) }
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
        Log.d("AlarmWorker", "Scheduling alarm with tag: $uniqueId")

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
        Log.d("AlarmWorker", "Deleting scheduled alarm with tag: $uniqueTag")

        viewModelScope.launch(Dispatchers.IO) {
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
}