package com.example.weathersync.data.local

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.FavoriteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class LocalFavoriteWeatherDataSource (private val dao: FavoriteDao): LocalDataSource.ILocalFavoriteWeatherDataSource {

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        withContext(Dispatchers.IO) {
            dao.insertFavorite(favorite)
        }
    }

    override suspend fun deleteFavorite(lat: Double, lon: Double) {
        withContext(Dispatchers.IO) {
            dao.deleteFavorite(lat, lon)
        }
    }

    override suspend fun getFavorite(lat: Double, lon: Double, ): FavoriteEntity? {
        return withContext(Dispatchers.IO) {
            dao.getFavorite(lat, lon)
        }
    }

    override fun getAllFavorites(): Flow<Response<List<FavoriteEntity>>> = flow {
        try {
            emit(Response.Loading)
            dao.getAllFavorites().collect {
                emit(Response.Success(it))
            }
            } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

}