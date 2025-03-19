package com.example.weathersync.data.model.remote


import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("coord")
    val coord: Coord? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("population")
    val population: Int? = null,
    @SerializedName("sunrise")
    val sunrise: Int? = null,
    @SerializedName("sunset")
    val sunset: Int? = null,
    @SerializedName("timezone")
    val timezone: Int? = null
)