package com.example.weathersync.data.repository

import com.example.weathersync.data.model.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeFavoriteRepository : FavoriteRepository {
    private val favoriteList = mutableListOf<FavoriteEntity>()
    private val favoritesFlow = MutableStateFlow<List<FavoriteEntity>>(emptyList())

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        favoriteList.add(favorite)
        favoritesFlow.value = favoriteList.toList()
    }

    override suspend fun deleteFavorite(lat: Double, lon: Double) {
        favoriteList.removeAll { it.lat == lat && it.lon == lon }
        favoritesFlow.value = favoriteList.toList()
    }

    override suspend fun getFavorite(lat: Double, lon: Double): FavoriteEntity? {
        return favoriteList.find { it.lat == lat && it.lon == lon }
    }

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoritesFlow.asStateFlow()
    }
}


