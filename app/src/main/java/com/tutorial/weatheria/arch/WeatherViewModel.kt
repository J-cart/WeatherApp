package com.tutorial.weatheria.arch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.db.WeatherDao
import com.tutorial.weatheria.networkmodels.Current
import com.tutorial.weatheria.networkmodels.Location
import com.tutorial.weatheria.networkmodels.SearchLocationResponse
import com.tutorial.weatheria.networkmodels.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    val repository: MainRepository,
    val weatherDao: WeatherDao
) : ViewModel() {
    val weatherForecast = MutableLiveData<Resource<WeatherResponse>>()
    val searchLocationResult = MutableLiveData<Resource<SearchLocationResponse>>()
    val searchLocationWeatherResult = MutableLiveData<Resource<WeatherResponse>>()
    val frmDab = MutableLiveData<WeatherResponse>()
//    init {
//        updateWeather()
//    }


    suspend fun fromDb() {
        frmDab.value = weatherDao.getWeatherResponse()
        Wrapper(frmDab.value?.location!!,frmDab.value?.current)

    }


    data class Wrapper(val location: Location,val current: Current? = null)

    fun updateWeather(location: String) {
        viewModelScope.launch {
            weatherForecast.value = Resource.Loading()
            when (val forecast = repository.getWeatherForecast(location = location, 7)) {
                is Resource.Successful -> {
                    forecast.data?.let {
                        weatherForecast.value = Resource.Successful(it)
                        weatherDao.insertWeatherResponse(it)
                        fromDb()
                    }
                }
                is Resource.Failure -> {
                    forecast.msg?.let {
                        weatherForecast.value = Resource.Failure(it)
                    }
                }
                is Resource.Empty -> {
                    weatherForecast.value = Resource.Empty()
                }
                else -> Unit
            }
        }
    }

    fun updateLocation(name: String) {
        viewModelScope.launch {
            searchLocationResult.value = Resource.Loading()
            when (val searchLocation = repository.getSearchedLocation(name)) {
                is Resource.Successful -> {
                    searchLocation.data?.let {
                        searchLocationResult.value = Resource.Successful(it)
                    }
                }
                is Resource.Failure -> {
                    searchLocation.msg?.let {
                        searchLocationResult.value = Resource.Failure(it)
                    }
                }
                is Resource.Empty -> {
                    searchLocationResult.value = Resource.Empty()
                }
            }
        }
    }

    fun updateWeatherSearchedLocation(location: String) {
        viewModelScope.launch {
            searchLocationWeatherResult.value = Resource.Loading()
            when (val forecast = repository.getWeatherForSearchedLocation(location = location, 7)) {
                is Resource.Successful -> {
                    forecast.data?.let {
                        searchLocationWeatherResult.value = Resource.Successful(it)
                    }
                }
                is Resource.Failure -> {
                    forecast.msg?.let {
                        searchLocationWeatherResult.value = Resource.Failure(it)
                    }
                }
                is Resource.Empty -> {
                    searchLocationWeatherResult.value = Resource.Empty()
                }
                else -> Unit
            }
        }
    }

}