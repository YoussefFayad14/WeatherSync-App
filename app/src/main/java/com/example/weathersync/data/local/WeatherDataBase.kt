package com.example.weathersync.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weathersync.data.model.local.Converters
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity

@Database(entities = [WeatherEntity::class, ForecastEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null
        fun getInstance(context: Context): WeatherDatabase {
            return instance ?: synchronized(this) {
                val INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                instance = INSTANCE
                INSTANCE
            }
        }
    }
}