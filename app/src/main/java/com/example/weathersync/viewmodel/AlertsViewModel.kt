package com.example.weathersync.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.AlarmEntity
import com.example.weathersync.data.model.local.FavoriteEntity
import com.example.weathersync.data.repository.AlarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val repository: AlarmRepository
): ViewModel() {
    private val _alarms = MutableStateFlow<Response<List<AlarmEntity>>>(Response.Loading)
    val alarms = _alarms.asStateFlow()

    private val _nextAlarm = MutableStateFlow<AlarmEntity?>(null)
    val nextAlarm: StateFlow<AlarmEntity?> get() = _nextAlarm

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    fun insertAlarm(timeMillis: Long) {
        viewModelScope.launch {
            repository.insertAlarm(AlarmEntity(timeMillis = timeMillis))
        }
    }

    fun deleteAlarm(alarmId: Int){
        viewModelScope.launch {
            repository.deleteAlarm(alarmId)
        }
    }

    fun deletePastAlarms(){
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            repository.deletePastAlarms(currentTime)
        }
    }

    fun getNextAlarm(){
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val nextAlarm = repository.getNextAlarm(currentTime)
            _nextAlarm.value = nextAlarm
        }
    }

    suspend fun getAlarmById(alarmId: Int): AlarmEntity? {
        return repository.getAlarmById(alarmId)
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

    fun enableAlarm(alarmId: Int) {
        viewModelScope.launch {
            repository.enableAlarm(alarmId)
        }
    }

    fun disableAlarm(alarmId: Int) {
        viewModelScope.launch {
            repository.disableAlarm(alarmId)
        }
    }
}