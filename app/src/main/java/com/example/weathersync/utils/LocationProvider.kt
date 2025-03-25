package com.example.weathersync.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.Locale

class LocationProvider(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
        .setMinUpdateIntervalMillis(2000)
        .build()

    private var locationCallback: LocationCallback? = null

    fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(
        callback: (latitude: Double, longitude: Double) -> Unit,
        onError: (message: String) -> Unit,
        activity: Activity
    ) {
        if (!checkPermission()) {
            requestPermission(activity)
            onError("Location permission required")
            return
        }

        if (!isGpsEnabled()) {
            onError("GPS is disabled")
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location: Location in locationResult.locations) {
                    callback(location.latitude, location.longitude)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            onError("Location permission required")
        }
    }

    fun getAddress(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(latitude, longitude, 1)?.getOrNull(0)
            ?.getAddressLine(0)
            ?.let { address ->
                address
                    .split(", ")
                    .takeLast(3)
                    .let { listOf(it.first(), it[1].split(" ").first(), it.last()) }
                    .joinToString(", ")
            } ?: "Unknown Address"
    }

}
