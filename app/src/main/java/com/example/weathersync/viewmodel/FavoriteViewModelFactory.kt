package com.example.weathersync.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathersync.data.local.LocalFavoriteWeatherDataSource
import com.example.weathersync.data.local.WeatherDatabase
import com.example.weathersync.data.repository.FavoriteRepositoryImpl

class FavoriteViewModelFactory (private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(
                context,
                FavoriteRepositoryImpl(
                    LocalFavoriteWeatherDataSource(WeatherDatabase.getInstance(context).favoriteDao())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}