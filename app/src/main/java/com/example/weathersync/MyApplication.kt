package com.example.weathersync

import android.app.Application
import android.content.Context
import com.example.weathersync.utils.LocaleHelper

class MyApplication : Application(){
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
}
