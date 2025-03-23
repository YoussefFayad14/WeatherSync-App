package com.example.weathersync.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weathersync.data.model.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite_table WHERE lat = :lat AND lon = :lon")
    suspend fun deleteFavorite(lat: Double, lon: Double)

    @Query("SELECT * FROM favorite_table WHERE lat = :lat AND lon = :lon")
    suspend fun getFavorite(lat: Double, lon: Double): FavoriteEntity?

    @Query("SELECT * FROM favorite_table")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

}