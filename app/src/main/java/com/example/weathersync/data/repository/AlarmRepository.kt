package com.example.weathersync.data.repository

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlarmRepository{
    suspend fun insertAlarm(alarm: AlarmEntity)
    suspend fun deleteAlarm(alarmId: Int)
    suspend fun deletePastAlarms(currentTime: Long)
    suspend fun getNextAlarm(currentTime: Long): AlarmEntity?
    suspend fun getAlarmById(alarmId: Int): AlarmEntity?
    fun getAllAlarms(): Flow<Response<List<AlarmEntity>>>
    suspend fun disableAlarm(alarmId: Int)
    suspend fun enableAlarm(alarmId: Int)
}