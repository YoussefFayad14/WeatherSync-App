package com.example.weathersync.data.model.remote


import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h")
    val h: Double? = null
)