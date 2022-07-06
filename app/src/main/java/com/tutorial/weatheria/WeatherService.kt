package com.tutorial.weatheria

import com.tutorial.weatheria.networkmodels.SearchLocationResponse
import com.tutorial.weatheria.networkmodels.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService{
    @GET("forecast.json")
    suspend fun getWeatherForecast(
        @Query("key")api:String = "5441365b93274a0e84a204801222906",
        @Query("q")location:String,
        @Query("days")days:Int
    ):Response<WeatherResponse>


    @GET("search.json")
    suspend fun getCurrentWeather(
        @Query("key")api:String = "5441365b93274a0e84a204801222906",
        @Query("q")location:String,
    ):Response<SearchLocationResponse>
}