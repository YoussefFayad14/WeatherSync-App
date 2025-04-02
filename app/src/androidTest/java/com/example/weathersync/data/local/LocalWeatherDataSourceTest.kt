package com.example.weathersync.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weathersync.data.model.local.WeatherEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalWeatherDataSourceTest {
    private lateinit var weatherDatabase: WeatherDatabase
    private lateinit var weatherDao: WeatherDao
    private lateinit var localWeatherDataSource: LocalWeatherDataSource

    @Before
    fun setUp() {
        weatherDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        weatherDao = weatherDatabase.weatherDao()
        localWeatherDataSource = LocalWeatherDataSource(weatherDao)
    }

    @After
    fun tearDown() {
        weatherDatabase.close()
    }

    @Test
    fun saveWeather_retrievesWeather() = runTest {
        // Given a weather entity to save
        val weatherEntity = WeatherEntity(
            id = 1,
            coordLat = 12.34,
            coordLon = 56.78,
            address = "Sample Address",
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
        localWeatherDataSource.saveWeather(weatherEntity)

        // When the weather is retrieved
        val result = localWeatherDataSource.getCachedWeather().firstOrNull()

        // Then the result should contain the saved weather entity
        assertThat(1, `is`(result?.size))
        assertThat(result?.get(0), `is`(weatherEntity))
        assertThat(result?.get(0)?.id, `is`(weatherEntity.id))
        assertThat(result?.get(0)?.address, `is`(weatherEntity.address))
        assertThat(result?.get(0)?.main, `is`(weatherEntity.main))
    }


    @Test
    fun getWeatherList_returnsListOfWeatherEntities() = runTest {
        // Given a weather entity to save
        val weather1 = WeatherEntity(
            id = 1,
            coordLat = 12.34,
            coordLon = 56.78,
            address = "Sample Address",
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
        val weather2 = WeatherEntity(
            id = 2,
            coordLat = 12.34,
            coordLon = 56.7,
            address = "Sample Address2",
            main = "Sunny",
            description = "Clear sky",
            icon = "02d",
            temp = 30.0,
            tempMin = 20.0,
            tempMax = 30.0,
            feelsLike = 24.0,
            pressure = 1015,
            humidity = 60,
            speed = 5.0,
            clouds = 0
        )
        // Save the weather entity
        localWeatherDataSource.saveWeather(weather1)
        localWeatherDataSource.saveWeather(weather2)

        // When the weather list is retrieved
        val result = localWeatherDataSource.getWeatherList()

        // Then the result should contain the saved weather entities
        assertThat(result.size, `is`(2))
        assertThat(result[0], `is`(weather1))
        assertThat(result[1], `is`(weather2))
        assertThat(result[0].id, `is`(weather1.id))
        assertThat(result[1].id, `is`(weather2.id))
    }

    @Test
    fun clearWeather_deletesAllWeatherEntities() = runTest {
        // Given a weather entity to save
        val weather = WeatherEntity(
            id = 1,
            coordLat = 12.34,
            coordLon = 56.78,
            address = "Sample Address",
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
        // Save the weather entity
        localWeatherDataSource.saveWeather(weather)

        // When the weather is cleared
        localWeatherDataSource.clearWeather()

        // Then the weather list should be empty
        val result = localWeatherDataSource.getCachedWeather().firstOrNull()
        assertThat(result?.size, `is`(0))
    }
}