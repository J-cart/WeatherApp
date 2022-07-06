package com.tutorial.weatheria.arch

import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.networkmodels.SearchLocationResponse
import com.tutorial.weatheria.networkmodels.WeatherResponse

interface MainRepository {
    suspend fun getWeatherForecast(location:String,days:Int):Resource<WeatherResponse>
    suspend fun getSearchedLocation(name:String):Resource<SearchLocationResponse>
    suspend fun getWeatherForSearchedLocation(location:String,days: Int):Resource<WeatherResponse>
}