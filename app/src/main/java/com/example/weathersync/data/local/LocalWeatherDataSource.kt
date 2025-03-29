package com.example.weathersync.data.local

import androidx.compose.ui.window.Popup
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalWeatherDataSource(private val dao: WeatherDao): LocalDataSource.ILocalWeatherDataSource {

    override suspend fun saveWeather(weather: WeatherEntity) {
        withContext(Dispatchers.IO) {
            dao.insertWeather(weather)
        }
    }

    override fun getCachedWeather(): Flow<Response<List<WeatherEntity>>> = flow {
        try {
            emit(Response.Loading)
            dao.getWeather().collect {
                emit(Response.Success(it))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun getWeatherList(): Response<List<WeatherEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                Response.Success(dao.getWeatherList())
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }
    }

    override suspend fun saveForecast(forecastList: ForecastEntity) {
        withContext(Dispatchers.IO) {
            dao.insertForecast(forecastList)
        }
    }

    override fun getCachedForecast(): Flow<Response<List<ForecastEntity>>> = flow{
        try {
            emit(Response.Loading)
            dao.getForecast().collect {
                emit(Response.Success(it))
            }
        }catch (e: Exception){
            emit(Response.Failure(e))
        }
    }

    override suspend fun getLastLocation(): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            dao.getLastLocation()?.let { locationData ->
                Pair(locationData.coordLat, locationData.coordLon)
            } ?: Pair(0.0, 0.0)
        }
    }

    override suspend fun clearWeather() {
        withContext(Dispatchers.IO) {
            dao.clearWeather()
        }
    }

    override suspend fun clearForecast() {
        withContext(Dispatchers.IO) {
            dao.clearForecast()
        }
    }

    override suspend fun getLastUpdatedWeather(): Long? {
        return withContext(Dispatchers.IO) {
            dao.lastUpdatedWeather()
        }
    }

    override suspend fun getLastUpdatedForecast(): Long? {
        return withContext(Dispatchers.IO) {
            dao.lastUpdatedForecast()
        }
    }

}
