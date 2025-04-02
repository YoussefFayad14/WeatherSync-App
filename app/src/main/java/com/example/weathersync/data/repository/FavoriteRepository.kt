package com.example.weathersync.data.repository

import com.example.weathersync.data.model.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun insertFavorite(favorite: FavoriteEntity)
    suspend fun deleteFavorite(lat: Double, lon: Double)
    suspend fun getFavorite(lat: Double, lon: Double): FavoriteEntity?
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
}