package com.example.weathersync.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathersync.data.model.local.WeatherEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
        weatherDao = database.weatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertWeather_successfully_insertsWeather() = runTest {
        // Given a weather entity to insert
        val weatherEntity = WeatherEntity(
            id = 1,
            coordLat = 12.34,
            coordLon = 56.78,
            address = "Test Address",
            main = "Clear",
            description = "Clear sky",
            icon = "01d",
            temp = 25.0,
            tempMin = 20.0,
            tempMax = 30.0,
            feelsLike = 24.0,
            pressure = 1015,
            humidity = 60,
            speed = 5.0,
            clouds = 0
        )

        // When the weather is inserted into the database
        weatherDao.insertWeather(weatherEntity)

        // Then the weather is retrieved from the database
        val weatherList = weatherDao.getWeatherList()
        assertEquals(1, weatherList.size)
        assertEquals(weatherEntity.id, weatherList[0].id)
    }


    @Test
    fun testDeleteWeather_successfully_clearsWeatherCache() = runTest {
        // Given two weather entities to insert
        val weatherEntity1 = WeatherEntity(
            id = 1,
            coordLat = 12.34,
            coordLon = 56.78,
            address = "Test Address 1",
            main = "Clear",
            description = "Clear sky",
            icon = "01d",
            temp = 25.0,
            tempMin = 20.0,
            tempMax = 30.0,
            feelsLike = 24.0,
            pressure = 1015,
            humidity = 60,
            speed = 5.0,
            clouds = 0
        )

        val weatherEntity2 = WeatherEntity(
            id = 2,
            coordLat = 23.45,
            coordLon = 67.89,
            address = "Test Address 2",
            main = "Cloudy",
            description = "Overcast",
            icon = "02d",
            temp = 18.0,
            tempMin = 16.0,
            tempMax = 20.0,
            feelsLike = 17.0,
            pressure = 1013,
            humidity = 75,
            speed = 3.0,
            clouds = 80
        )

        // When the weather is inserted into the database
        weatherDao.insertWeather(weatherEntity1)
        weatherDao.insertWeather(weatherEntity2)

        // Clear weather data
        weatherDao.deleteAllWeather()

        // Then the weather is retrieved from the database
        val weatherList = weatherDao.getWeatherList()
        assertEquals(0, weatherList.size)
    }


    @Test
    fun testGetWeatherFlow_successfully_retrievesWeather() = runTest {
        // Given a weather entity to insert
        val weatherEntity = WeatherEntity(
            id = 1,
            coordLat = 12.34,
            coordLon = 56.78,
            address = "Test Address",
            main = "Clear",
            description = "Clear sky",
            icon = "01d",
            temp = 25.0,
            tempMin = 20.0,
            tempMax = 30.0,
            feelsLike = 24.0,
            pressure = 1015,
            humidity = 60,
            speed = 5.0,
            clouds = 0
        )

        // When the weather is inserted into the database
        weatherDao.insertWeather(weatherEntity)

        // Collect from the weather flow
        val weatherList = weatherDao.getWeather().first()

        // Then the weather is retrieved from the database
        assertEquals(1, weatherList.size)
        assertEquals(weatherEntity.id, weatherList[0].id)
    }
}

