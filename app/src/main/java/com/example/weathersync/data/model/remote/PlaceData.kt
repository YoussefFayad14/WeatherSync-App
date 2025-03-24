package com.example.weathersync.data.model.remote

import com.google.android.gms.maps.model.LatLng

data class PlaceData(
    val placeId: String,
    val displayName: String?,
    val latLng: LatLng? = null,
    val address: String? = null
)