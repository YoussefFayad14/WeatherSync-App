package com.example.weathersync.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import com.example.weathersync.R
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, language: String): Context {
        //SharedPreferencesHelper.saveSetting(context, SharedPreferencesHelper.KEY_LANGUAGE, language)
        Log.d("LocaleHelper", "Setting language to: $language")
        return updateResources(context, language)
    }

    fun onAttach(context: Context): Context {
        var language = SharedPreferencesHelper.getSetting(context, SharedPreferencesHelper.KEY_LANGUAGE)
        /*if (language == "Default"){
            val defaultLanguage = context.resources.configuration.locales[0].language
            if (defaultLanguage == "ar") {
                 language = "Arabic"
            } else {
                language = "English"
            }
        }*/
        Log.d("LocaleHelper", "Attaching language: $language")
        return updateResources(context, language)
    }

    fun covert(st : String) : Locale {
        if(st == "ar"){
            return Locale("ar")
        }
        else
            return Locale("en")

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun updateResources(context: Context, language: String): Context {
        Log.d("LocaleHelper", "Updating language to: $language")
        val locale = when (language) {
            "Arabic" -> Locale("ar")
            "English" -> Locale("en")
            "Default" -> covert(context.resources.configuration.locales[0].language)
            else -> Locale("en")
        }
        Log.d("LocaleHelper", "Selected locale: $locale")

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
