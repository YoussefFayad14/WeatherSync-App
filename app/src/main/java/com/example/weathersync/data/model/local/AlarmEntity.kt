package com.example.weathersync.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeMillis: Long
)
