package com.tutorial.weatheria.arch

import android.util.Log
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.WeatherService
import com.tutorial.weatheria.networkmodels.SearchLocationResponse
import com.tutorial.weatheria.networkmodels.WeatherResponse
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(val api: WeatherService) : MainRepository {
    override suspend fun getWeatherForecast(
        location: String,
        days: Int
    ): Resource<WeatherResponse> {
        return try {
            val result = api.getWeatherForecast(location = location, days = days)
            val response = result.body()!!
            Log.d("RESPONSE-S", "$response")
            Log.d("LOCATION", "${response.location}")
            Resource.Successful(response)
        } catch (e: Exception) {
            Log.d("RESPONSE-F", "$e")
            Resource.Failure("AN EXCEPTION OCCURRED <---> ${e.message}")
        }

    }

    override suspend fun getSearchedLocation(name:String): Resource<SearchLocationResponse> {
        return try {
            val result = api.getCurrentWeather(location = name)
            val response = result.body()!!
            Log.d("RESPONSE-S-search", "$response")
            Log.d("LOCATION", "${response}")
            Resource.Successful(response)
        }catch (e:Exception){
            Log.d("RESPONSE-F-search", "$e")
            Resource.Failure("$e")
        }
    }

    override suspend fun getWeatherForSearchedLocation(
        location: String,
        days: Int
    ): Resource<WeatherResponse> {
        return try {
            val result = api.getWeatherForecast(location = location, days = days)
            val response = result.body()!!
            Log.d("RESPONSE-S-w", "$response")
            Log.d("LOCATION", "${response.location.name}")
            Resource.Successful(response)
        } catch (e: Exception) {
            Log.d("RESPONSE-F-W", "$e")
            Resource.Failure("AN EXCEPTION OCCURRED <---> ${e.message}")
        }
    }
}