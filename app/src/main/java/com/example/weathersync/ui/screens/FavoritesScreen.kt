package com.example.weathersync.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.weathersync.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.weathersync.data.model.Response
import com.example.weathersync.ui.components.FavoriteItem
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.DeepNavyBlue1
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.viewmodel.FavoriteViewModel

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritesScreen(navController: NavHostController, favoriteViewModel: FavoriteViewModel) {
    val favoriteState by favoriteViewModel.favorites.collectAsStateWithLifecycle()
    val favoriteItems = (favoriteState as? Response.Success)?.data ?: emptyList()
    val message by favoriteViewModel.message.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        favoriteViewModel.getFavorites()
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("map_screen") },
                containerColor = if (isSystemInDarkTheme()) DeepNavyBlue1 else LightSeaGreen
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_favorite_white),
                    contentDescription = "Add Alarm Icon",
                    contentScale = ContentScale.Crop
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favoriteItems, key = { "${it.lat},${it.lon}" }) { item ->
                FavoriteItem(
                    item,
                    navigateTo = {  },
                    onRemove = { favoriteViewModel.deleteFavorite(item.lat, item.lon)},
                    modifier = Modifier.animateItemPlacement(tween(200))
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FavoritesScreenPreview(){
   // FavoritesScreen(navController = NavHostController(LocalContext.current))
}
