package com.example.weathersync.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.example.weathersync.R
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, language: String): Context {
        SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_LANGUAGE, language)
        return updateResources(context, language)
    }

    fun onAttach(context: Context): Context {
        val language = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
            .ifEmpty { context.getString(R.string.default_language) }
        return updateResources(context, language)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun updateResources(context: Context, language: String): Context {
        val locale = when (language) {
            context.getString(R.string.arabic) -> Locale("ar")
            context.getString(R.string.english) -> Locale("en")
            else -> Locale.getDefault()
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
}
