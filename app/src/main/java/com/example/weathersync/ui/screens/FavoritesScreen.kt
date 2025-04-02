package com.example.weathersync.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.weathersync.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.local.FavoriteEntity
import com.example.weathersync.navigation.ScreenRoute.MapScreenRoute
import com.example.weathersync.navigation.ScreenRoute.WeatherDetailsScreenRoute
import com.example.weathersync.ui.components.AnimatedSnackBar
import com.example.weathersync.ui.components.FavoriteItem
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.DeepNavyBlue1
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.utils.DrawableUtils
import com.example.weathersync.viewmodel.FavoriteViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritesScreen(navController: NavHostController, favoriteViewModel: FavoriteViewModel) {
    val context = LocalContext.current
    val favorites by favoriteViewModel.favorites.collectAsStateWithLifecycle()
    val message by favoriteViewModel.message.collectAsStateWithLifecycle()
    var showSnackBar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var deletedFavorite by remember { mutableStateOf<FavoriteEntity?>(null) }


    LaunchedEffect(Unit) {
        favoriteViewModel.getFavorites()
    }

    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            snackbarMessage = message
            showSnackBar = true
            delay(5000)
            if (showSnackBar) {
                favoriteViewModel.clearMessage()
                showSnackBar = false
                deletedFavorite = null
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(MapScreenRoute.createRoute(null, null, false)) },
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
            when(favorites) {
                is Response.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = if (isSystemInDarkTheme()) LightSeaGreen else DeepNavyBlue)
                        }
                    }
                }
                is Response.Success -> {
                    val favoriteItems = (favorites as Response.Success).data
                    if (favoriteItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val composition by rememberLottieComposition(
                                        LottieCompositionSpec.RawRes(DrawableUtils.getAnimationDrawable("no_alarm_set"))
                                    )
                                    val progress by animateLottieCompositionAsState(composition)

                                    LottieAnimation(
                                        composition = composition,
                                        progress = { progress },
                                        modifier = Modifier.size(200.dp)
                                    )

                                    Text(
                                        text = stringResource(R.string.no_favorites_set_yet),
                                        fontSize = 24.sp,
                                        color = Color.White ,
                                    )
                                }
                            }
                        }
                    } else {
                        items(favoriteItems, key = { "${it.lat},${it.lon}" }) { item ->
                            FavoriteItem(
                                item,
                                onDelete = {
                                    deletedFavorite = item
                                    favoriteViewModel.deleteFavorite(item.lat, item.lon)
                                    favoriteViewModel.setMessage(context.getString(R.string.favorite_weather_removed))
                                },
                                modifier = Modifier
                                    .animateItemPlacement(tween(200))
                                    .clickable {
                                        navController.navigate(WeatherDetailsScreenRoute.createRoute(item.weatherEntity))
                                    }
                            )
                        }
                    }

                }
                is Response.Failure -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val errorMessage =
                                (favorites as Response.Failure).error.message ?: "Unknown error"
                            Text(text = "Error: $errorMessage", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
        AnimatedSnackBar(
            message = if (showSnackBar) snackbarMessage else null,
            type = "Success",
            showUndoButton = deletedFavorite != null,
            onUndoClick = {
                deletedFavorite?.let { favoriteViewModel.insertFavorite(it.lat, it.lon) }
                showSnackBar = false
                favoriteViewModel.clearMessage()
                deletedFavorite = null
            }
        )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FavoritesScreenPreview(){
   // FavoritesScreen(navController = NavHostController(LocalContext.current))
}
