package com.example.weathersync.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weathersync.data.model.local.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Query("DELETE FROM alarm_table WHERE id = :alarmId")
    suspend fun deleteAlarm(alarmId: Int)

    @Query("DELETE FROM alarm_table WHERE timeMillis < :currentTime")
    suspend fun deletePastAlarms(currentTime: Long)

    @Query("SELECT * FROM alarm_table WHERE timeMillis >= :currentTime ORDER BY timeMillis ASC LIMIT 1")
    suspend fun getNextAlarm(currentTime: Long): AlarmEntity?

    @Query("SELECT * FROM alarm_table WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Int): AlarmEntity?

    @Query("SELECT * FROM alarm_table ORDER BY timeMillis ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Query("UPDATE alarm_table SET isEnabled = 0 WHERE id = :alarmId")
    suspend fun disableAlarm(alarmId: Int)

    @Query("UPDATE alarm_table SET isEnabled = 1 WHERE id = :alarmId")
    suspend fun enableAlarm(alarmId: Int)
}
