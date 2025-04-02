package com.example.weathersync.data.repository

import com.example.weathersync.data.local.LocalFavoriteWeatherDataSource
import com.example.weathersync.data.model.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow

class FavoriteRepositoryImpl (
    private val localFavoriteWeatherDataSource: LocalFavoriteWeatherDataSource
): FavoriteRepository {

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        return localFavoriteWeatherDataSource.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(lat: Double, lon: Double) {
        return localFavoriteWeatherDataSource.deleteFavorite(lat, lon)
    }

    override suspend fun getFavorite(lat: Double, lon: Double): FavoriteEntity? {
        return localFavoriteWeatherDataSource.getFavorite(lat, lon)
    }

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return localFavoriteWeatherDataSource.getAllFavorites()
    }
}