package com.example.weathersync.data.local

import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.FavoriteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class LocalFavoriteWeatherDataSource (private val dao: FavoriteDao): LocalDataSource.ILocalFavoriteWeatherDataSource {

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        dao.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(lat: Double, lon: Double) {
        dao.deleteFavorite(lat, lon)
    }

    override suspend fun getFavorite(lat: Double, lon: Double, ): FavoriteEntity? {
        return dao.getFavorite(lat, lon)
    }

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return dao.getAllFavorites()
    }

}