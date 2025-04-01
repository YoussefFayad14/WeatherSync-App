package com.example.weathersync.ui.screens

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.weathersync.R
import com.example.weathersync.navigation.ScreenRoute.SettingsScreenRoute
import com.example.weathersync.ui.components.AnimatedSnackBar
import com.example.weathersync.ui.components.BottomSheetContent
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.utils.LocationProvider
import com.example.weathersync.utils.SettingUtils
import com.example.weathersync.utils.SharedPreferencesHelper
import com.example.weathersync.viewmodel.FavoriteViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    lat: Double?,
    lon: Double?,
    isSettingsChanged: Boolean = false
) {
    val context = LocalContext.current
    val activity = remember { context as? Activity }
    val locationProvider = remember { LocationProvider(context) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var marker by remember { mutableStateOf<Marker?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val initialLocation = lat?.let { LatLng(it, lon ?: 0.0) }
    var isLocationConfirmed by rememberSaveable { mutableStateOf(false) }


    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isLocationConfirmed && isSettingsChanged) {
                    // User backed out without selecting a location, revert settings
                    val storeLocationType = SettingUtils.toSharedPreferencesLocation("Gps")
                    SharedPreferencesHelper.saveSetting(
                        context,
                        SharedPreferencesHelper.KEY_LOCATION_TYPE,
                        storeLocationType
                    )
                }
                navController.popBackStack()
            }
        }
    }

    DisposableEffect(backDispatcher) {
        backDispatcher?.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }

    LaunchedEffect(Unit) {
        if (initialLocation != null) {
            selectedLocation = initialLocation
            showBottomSheet = true
        } else {
            activity?.let {
                locationProvider.getUserLocation(
                    callback = { userLat, userLon -> userLocation = LatLng(userLat, userLon) },
                    onError = { Log.e("LocationError", it) },
                    activity = it
                )
            }
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
                    MaterialTheme.shapes.medium
                )
                .padding(12.dp)
                .clickable { navController.navigate("search_screen") }
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = Color.White,
                )
                Text(
                    text = stringResource(R.string.search_location),
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp),
                    maxLines = 1,
                )
            }
        }
        AndroidView(
            factory = { context ->
                MapView(context).apply { onCreate(null) }
            },
            modifier = Modifier.fillMaxSize(),
            update = { mapView ->
                mapView.getMapAsync { googleMap ->
                    setupMap(googleMap, initialLocation ?: userLocation, selectedLocation) { newLocation, newMarker ->
                        marker?.remove()
                        marker = newMarker
                        selectedLocation = newLocation
                        showBottomSheet = true
                        isLocationConfirmed = true
                    }
                }
            }
        )
        if (showBottomSheet && selectedLocation != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState
            ) {
                BottomSheetContent(
                    selectedLocation = selectedLocation!!,
                    onCancel = { showBottomSheet = false },
                    onSave = {
                        selectedLocation?.let { location ->
                            Log.d("MapScreen", "Saving location: $isSettingsChanged")
                            if (isSettingsChanged && isLocationConfirmed) {
                                SharedPreferencesHelper.saveLocation(
                                    context,
                                    location.latitude,
                                    location.longitude
                                )
                                showBottomSheet = false
                                navController.navigate(SettingsScreenRoute.createRoute(context.getString(R.string.location_saved_successfully))) {
                                    popUpTo("map_screen") { inclusive = true }
                                }
                            } else if (!isSettingsChanged) {
                                favoriteViewModel.insertFavorite(
                                    location.latitude,
                                    location.longitude
                                )
                                showBottomSheet = false
                                navController.navigate("favorites_screen")
                            }
                        }
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
    onLocationSelected: (LatLng, Marker) -> Unit,
) {
    googleMap.uiSettings.isZoomControlsEnabled = true
    googleMap.uiSettings.isMyLocationButtonEnabled = true

    val defaultLocation = LatLng(31.2001, 29.9187) // Alexandria, Egypt
    val locationToShow = userLocation ?: defaultLocation

    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationToShow, 12f))

    googleMap.clear()

    selectedLocation?.let {
        googleMap.addMarker(MarkerOptions().position(it).title("Selected Location"))
    }

    googleMap.setOnMapClickListener { latLng ->
        googleMap.clear()
        val clickedMarker = googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
        onLocationSelected(latLng, clickedMarker!!)
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            val clickedMarker = googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            onLocationSelected(latLng, clickedMarker!!)
        }
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
