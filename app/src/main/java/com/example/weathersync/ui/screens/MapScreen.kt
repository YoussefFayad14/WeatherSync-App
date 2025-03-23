package com.example.weathersync.ui.screens

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.weathersync.ui.components.BottomSheetContent
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.utils.LocationProvider
import com.example.weathersync.viewmodel.FavoriteViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, favoriteViewModel: FavoriteViewModel) {
    val context = LocalContext.current
    val activity = remember { context as? Activity }
    val mapView = rememberMapViewWithLifecycle()
    val locationProvider = remember { LocationProvider(context) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        activity?.let {
            locationProvider.getUserLocation(
                callback = { lat, lon -> userLocation = LatLng(lat, lon) },
                onError = { Log.e("LocationError", it) },
                activity = it
            )
        }
    }

    Column(
        modifier = Modifier
            .background(if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(
                    1.dp,
                    if (isSystemInDarkTheme()) LightSeaGreen else Color.White,
                    MaterialTheme.shapes.medium)
                .padding(12.dp)
                .clickable { navController.navigate("search_screen") }
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                )
                Text(
                    text = "Search Location",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp),
                    maxLines = 1,
                )
            }
        }

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ) { mapView ->
            mapView.getMapAsync { googleMap ->
                setupMap(googleMap, userLocation, selectedLocation) { newLocation ->
                    selectedLocation = newLocation
                    showBottomSheet = true
                }
            }
        }
        if (showBottomSheet && selectedLocation != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState
            ) {
                BottomSheetContent(
                    selectedLocation = selectedLocation!!,
                    onCancel = { showBottomSheet = false },
                    onSave = {
                        Log.d("MapScreen", "Saved Location: ${selectedLocation!!.latitude}, ${selectedLocation!!.longitude}")
                        favoriteViewModel.insertFavorite(selectedLocation?.latitude, selectedLocation?.longitude)
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

private fun setupMap(
    googleMap: GoogleMap,
    userLocation: LatLng?,
    selectedLocation: LatLng?,
    onLocationSelected: (LatLng) -> Unit
) {
    googleMap.uiSettings.isZoomControlsEnabled = true
    googleMap.uiSettings.isMyLocationButtonEnabled = true

    val defaultLocation = LatLng(31.2001, 29.9187) // Alexandria, Egypt
    val locationToShow = userLocation ?: defaultLocation

    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationToShow, 12f))

    var marker: Marker? = null

    selectedLocation?.let {
        marker = googleMap.addMarker(MarkerOptions().position(it).title("Selected Location"))
    }

    googleMap.setOnMapClickListener { latLng ->
        marker?.remove()
        marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
        onLocationSelected(latLng)
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context).apply { onCreate(Bundle()) } }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDestroy()
        }
    }

    return mapView
}
