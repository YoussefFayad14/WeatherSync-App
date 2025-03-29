package com.example.weathersync.data.local

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.AlarmEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class LocalAlarmDataSource(private val alarmDao: AlarmDao) : LocalDataSource.ILocalAlarmDataSource {
    override suspend fun insertAlarm(alarm: AlarmEntity) {
        withContext(Dispatchers.IO) {
            alarmDao.insertAlarm(alarm)
        }
    }

    override suspend fun deleteAlarm(alarmId: Int) {
        withContext(Dispatchers.IO) {
            alarmDao.deleteAlarm(alarmId)
        }
    }

    override suspend fun deletePastAlarms(currentTime: Long) {
        withContext(Dispatchers.IO) {
            alarmDao.deletePastAlarms(currentTime)
        }
    }

    override suspend fun getAlarmById(alarmId: Int): AlarmEntity? {
        return withContext(Dispatchers.IO) {
            alarmDao.getAlarmById(alarmId)
        }
    }

    override fun getAllAlarms(): Flow<Response<List<AlarmEntity>>> = flow{
        try {
            emit(Response.Loading)
            alarmDao.getAllAlarms().collect {
                emit(Response.Success(it))
            }
        }catch (e :Exception){
            emit(Response.Failure(e))
        }
    }
}