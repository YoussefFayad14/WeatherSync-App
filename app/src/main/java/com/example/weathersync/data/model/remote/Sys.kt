package com.example.weathersync.data.model.remote


import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("pod")
    val pod: String? = null
)