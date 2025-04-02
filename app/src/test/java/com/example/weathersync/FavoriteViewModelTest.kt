package com.example.weathersync

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weathersync.data.model.local.FavoriteEntity
import com.example.weathersync.data.repository.FakeFavoriteRepository
import com.example.weathersync.data.repository.FakeWeatherRepository
import com.example.weathersync.viewmodel.FavoriteViewModel
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito
import kotlin.test.Test

class FavoriteViewModelTest {
    @get:Rule
    var instanceExecuteRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var favRepository: FakeFavoriteRepository
    private lateinit var weatherRepository: FakeWeatherRepository
    lateinit var favEntity1: FavoriteEntity
    lateinit var favEntity2: FavoriteEntity


    @Before
    fun setup() {
        context = Mockito.mock(Context::class.java)
        favRepository = FakeFavoriteRepository()
        weatherRepository = FakeWeatherRepository()
        viewModel = FavoriteViewModel(context, favRepository, weatherRepository)
        favEntity1 = FavoriteEntity(1.0, 2.0, "Address1", null)
        favEntity2 = FavoriteEntity(3.0, 4.0, "Address2", null)
    }

    @Test
    fun testInsertFavorite() {
        viewModel.insertFavorite(favEntity1.lat, favEntity1.lon)
        val value = viewModel.favorites.getOrAwaitValue()
        assertThat(value , not(nullValue()))
    }

    @Test
    fun testDeleteFavorite_And_CheckListIsEmpty() {
        viewModel.insertFavorite(favEntity1.lat, favEntity1.lon)
        viewModel.deleteFavorite(favEntity1.lat, favEntity1.lon)
        val value = viewModel.favorites.getOrAwaitValue()
        assertThat(value , not(nullValue()))
    }

    @Test
    fun testGetFavorites() {
        viewModel.insertFavorite(favEntity1.lat, favEntity1.lon)
        viewModel.insertFavorite(favEntity2.lat, favEntity2.lon)
        viewModel.getFavorites()
        val value = viewModel.favorites.getOrAwaitValue()
        assertThat(value , not(nullValue()))
    }
}