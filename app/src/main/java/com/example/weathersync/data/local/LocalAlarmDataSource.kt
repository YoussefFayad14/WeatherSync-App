package com.example.weathersync.data.local

import com.example.weathersync.data.model.local.AlarmEntity
import kotlinx.coroutines.flow.Flow

class LocalAlarmDataSource(private val alarmDao: AlarmDao) : LocalDataSource.ILocalAlarmDataSource {
    override suspend fun insertAlarm(alarm: AlarmEntity) {
        alarmDao.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarmId: Int) {
        alarmDao.deleteAlarm(alarmId)
    }

    override suspend fun deletePastAlarms(currentTime: Long) {
        alarmDao.deletePastAlarms(currentTime)
    }

    override suspend fun getAlarmById(alarmId: Int): AlarmEntity? {
        return alarmDao.getAlarmById(alarmId)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return alarmDao.getAllAlarms()
    }
}