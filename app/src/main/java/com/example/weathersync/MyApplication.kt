package com.example.weathersync

import android.app.Application
import com.example.weathersync.utils.PLACES_API_KEY
import com.google.android.libraries.places.api.Places

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, PLACES_API_KEY)
        }
    }
}
