package com.tutorial.weatheria.network_and_data_models


import com.google.gson.annotations.SerializedName

data class Forecast(
    @SerializedName("forecastday")
    val forecastday: List<Forecastday>
)