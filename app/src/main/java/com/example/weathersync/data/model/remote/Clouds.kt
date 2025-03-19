package com.example.weathersync.data.model.remote


import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    val all: Int? = null
)