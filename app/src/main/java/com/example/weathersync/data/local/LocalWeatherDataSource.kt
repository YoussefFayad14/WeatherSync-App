package com.example.weathersync.data.local

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.ForecastEntity
import com.example.weathersync.data.model.local.WeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class LocalWeatherDataSource(private val dao: WeatherDao): LocalDataSource {

    override suspend fun saveWeather(weather: WeatherEntity) {
        withContext(Dispatchers.IO) {
            dao.clearWeather()
            dao.insertWeather(weather)
        }
    }

    override fun getCachedWeather(): Flow<Response<List<WeatherEntity>>> = flow {
        try {
            emit(Response.Loading)
            dao.getWeather().collect {
                emit(Response.Success(it))
            }
        }catch (e: Exception){
            emit(Response.Failure(e))
        }
    }

    override suspend fun saveForecast(forecastList: ForecastEntity) {
        withContext(Dispatchers.IO) {
            dao.clearForecast()
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

    override suspend fun getLastLocation(): Triple<Double, Double, Long> {
        return withContext(Dispatchers.IO) {
            dao.getLastLocation()?.let { locationData ->
                Triple(locationData.coordLat, locationData.coordLon, locationData.timestamp)
            } ?: Triple(0.0, 0.0, 0L)
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

}
