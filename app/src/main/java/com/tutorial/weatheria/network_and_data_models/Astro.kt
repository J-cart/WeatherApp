package com.tutorial.weatheria.network_and_data_models


import com.google.gson.annotations.SerializedName

data class Astro(
    @SerializedName("moon_illumination")
    val moonIllumination: String,
    @SerializedName("moon_phase")
    val moonPhase: String,
    @SerializedName("moonrise")
    val moonrise: String,
    @SerializedName("moonset")
    val moonset: String,
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String
)