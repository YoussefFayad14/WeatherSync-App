package com.example.weathersync.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.weathersync.BuildConfig
import com.example.weathersync.R
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.remote.PlaceData
import com.example.weathersync.navigation.ScreenRoute.MapScreenRoute
import com.example.weathersync.ui.components.SearchBar
import com.example.weathersync.ui.components.SearchResultItem
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.utils.PLACES_API_KEY
import com.example.weathersync.viewmodel.SearchViewModel
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, searchViewModel: SearchViewModel) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val searchResults by searchViewModel.places.collectAsStateWithLifecycle(initialValue = Response.Loading)
    val message by searchViewModel.message.collectAsStateWithLifecycle(initialValue = "")
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.PLACES_API_KEY)

    LaunchedEffect(searchQuery.text) {
        searchViewModel.updateSearchQuery(searchQuery.text)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = if (Locale.current.language == "ar") Icons.Default.ArrowForward else Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(start = 4.dp)
                )
            }
            SearchBar(
                searchQuery = searchQuery,
                onQueryChange = { newQuery ->
                    searchQuery = newQuery
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (searchResults) {
            is Response.Loading -> {
                CircularProgressIndicator(
                    color = if (isSystemInDarkTheme()) LightSeaGreen else DeepNavyBlue
                )
            }

            is Response.Success -> {
                val results = (searchResults as Response.Success<List<PlaceData>>).data
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(results.size) { index ->
                        val place = results[index]
                        Log.d("SearchScreen", "Result at index $index: ${place.displayName}")

                        SearchResultItem(
                            address = place.address ?: "Unknown",
                            onClick = {
                                Log.d("SearchScreen", "Clicked on result: ${place.displayName}")
                                scope.launch {
                                    val location = searchViewModel.fetchPlaceDetails(place.placeId)
                                    location?.let {
                                        navController.navigate(MapScreenRoute.createRoute(it.first, it.second,false))
                                    }
                                    Log.d("SearchScreen", "Fetched location: Lat: ${location?.first}, Lng: ${location?.second}")
                                }
                            }
                        )
                    }
                }
            }

            is Response.Failure -> {
                val errorMessage = (searchResults as Response.Failure).error.message ?: "Unknown error"
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchScreenPreview() {
    val fakeNavController = rememberNavController()
    val fakeSearchViewModel = SearchViewModel(
        context = fakeNavController.context,
    )
    SearchScreen(navController = fakeNavController, searchViewModel = fakeSearchViewModel)
}


