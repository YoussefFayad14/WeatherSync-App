package com.example.weathersync.data.repository

import com.example.weathersync.data.local.LocalAlarmDataSource
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.AlarmEntity
import kotlinx.coroutines.flow.Flow

class AlarmRepositoryImpl(
    private val localAlarmDataSource: LocalAlarmDataSource
): AlarmRepository {

    override suspend fun insertAlarm(alarm: AlarmEntity) {
        return localAlarmDataSource.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarmId: Int) {
        return localAlarmDataSource.deleteAlarm(alarmId)
    }

    override suspend fun deletePastAlarms(currentTime: Long) {
        return localAlarmDataSource.deletePastAlarms(currentTime)
    }

    override suspend fun getNextAlarm(currentTime: Long): AlarmEntity? {
        return localAlarmDataSource.getNextAlarm(currentTime)
    }

    override suspend fun getAlarmById(alarmId: Int): AlarmEntity? {
        return localAlarmDataSource.getAlarmById(alarmId)
    }

    override fun getAllAlarms(): Flow<Response<List<AlarmEntity>>> {
        return localAlarmDataSource.getAllAlarms()
    }
    override suspend fun disableAlarm(alarmId: Int) {
        return localAlarmDataSource.disableAlarm(alarmId)
    }
    override suspend fun enableAlarm(alarmId: Int) {
        return localAlarmDataSource.enableAlarm(alarmId)
    }
}