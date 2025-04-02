package com.example.weathersync.data.repository

import com.example.weathersync.data.local.LocalAlarmDataSource
import com.example.weathersync.data.model.local.AlarmEntity
import com.example.weathersync.data.repository.AlarmRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AlarmRepositoryImplTest {

    private lateinit var localAlarmDataSource: LocalAlarmDataSource
    private lateinit var alarmRepository: AlarmRepositoryImpl

    @Before
    fun setUp() {
        localAlarmDataSource = mockk(relaxed = true)
        alarmRepository = AlarmRepositoryImpl(localAlarmDataSource)
    }

    @Test
    fun testInsertAlarm_And_GetDataSource() = runBlocking {
        // Given
        val alarm = AlarmEntity(id = 1, System.currentTimeMillis())

        // Mock the insertAlarm method
        coEvery { localAlarmDataSource.insertAlarm(alarm) } returns Unit

        // When
        alarmRepository.insertAlarm(alarm)

        // Then
        coVerify { localAlarmDataSource.insertAlarm(alarm) }
    }

    @Test
    fun testDeleteAlarm_And_GetDataSource() = runBlocking {
        // Given
        val alarmId = 1

        // When
        alarmRepository.deleteAlarm(alarmId)

        // Then
        coVerify { localAlarmDataSource.deleteAlarm(alarmId) }
    }

}
