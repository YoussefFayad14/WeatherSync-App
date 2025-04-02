package com.example.weathersync.data.repository

import com.example.weathersync.data.model.local.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlarmRepository{
    suspend fun insertAlarm(alarm: AlarmEntity)
    suspend fun deleteAlarm(alarmId: Int)
    suspend fun deletePastAlarms(currentTime: Long)
    suspend fun getAlarmById(alarmId: Int): AlarmEntity?
    fun getAllAlarms(): Flow<List<AlarmEntity>>
}