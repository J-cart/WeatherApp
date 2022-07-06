package com.tutorial.weatheria.arch

import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import com.tutorial.weatheria.network_and_data_models.SearchLocationResponse
import com.tutorial.weatheria.network_and_data_models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun getWeatherForecast(location:String,days:Int):Resource<WeatherResponse>
    suspend fun getSearchedLocation(name:String):Resource<SearchLocationResponse>
    suspend fun getWeatherForSearchedLocation(location:String,days: Int):Resource<WeatherResponse>

    suspend fun getAllWeatherFromDb():WeatherResponse
    suspend fun insertAllWeather(weatherResponse: WeatherResponse)
    suspend fun deleteAllWeather()


    suspend fun getAllSavedWeather(): Flow<List<SavedWeather>>
    suspend fun insertAllSavedWeather(savedWeather: SavedWeather)
    suspend fun deleteSavedWeather(savedWeather: SavedWeather)
    suspend fun deleteAllSavedWeather()
}