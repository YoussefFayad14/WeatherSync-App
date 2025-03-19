package com.example.weathersync.data.model.remote


import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("city")
    val city: City? = null,
    @SerializedName("cnt")
    val cnt: Int? = null,
    @SerializedName("cod")
    val cod: String? = null,
    @SerializedName("list")
    val list: List<Item?>? = null,
    @SerializedName("message")
    val message: Int? = null
)