package com.tutorial.weatheria.arch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.db.WeatherDao
import com.tutorial.weatheria.db.WeatherDataBase
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import com.tutorial.weatheria.network_and_data_models.SearchLocationResponse
import com.tutorial.weatheria.network_and_data_models.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: MainRepository,
    private val weatherDao: WeatherDao,
    private val db:WeatherDataBase
) : ViewModel() {
    val weatherForecast = MutableLiveData<Resource<WeatherResponse>>()
    val searchLocationResult = MutableLiveData<Resource<SearchLocationResponse>>()
    val searchLocationWeatherResult = MutableLiveData<Resource<WeatherResponse>>()
    val frmDab = MutableLiveData<WeatherResponse>()
    val savedWeatherResult = MutableLiveData<List<SavedWeather>>()
//    init {
//        updateWeather()
//    }


    fun updateWeather(location: String) {
        weatherForecast.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val forecast = repository.getWeatherForecast(location = location, 7)) {
                is Resource.Successful -> {
                    forecast.data?.let {
                       // weatherForecast.value = Resource.Successful(it)
                        db.withTransaction {
                            deleteAllWeather()
                            insertAllWeather(it)
                        }
                    }
                }
                is Resource.Failure -> {
                    forecast.msg?.let {
                      //  weatherForecast.value = Resource.Failure(it)
                    }
                }
                is Resource.Empty -> {
                    //weatherForecast.value = Resource.Empty()
                }
                else -> Unit
            }
            getAllWeatherFromDb()
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

    //REGULAR FORECAST
    private fun getAllWeatherFromDb() = viewModelScope.launch {
        frmDab.value = repository.getAllWeatherFromDb()
    }

    private fun insertAllWeather(weatherResponse: WeatherResponse) = viewModelScope.launch {
        repository.insertAllWeather(weatherResponse)
    }

    private fun deleteAllWeather() = viewModelScope.launch {
        repository.deleteAllWeather()
    }


    //SAVED WEATHER
    private fun getAllSavedWeather() {
        viewModelScope.launch {
            repository.getAllSavedWeather().collect {
                savedWeatherResult.value = it
            }
        }
    }

    private fun insertAllSavedWeather(savedWeather: SavedWeather) {
        viewModelScope.launch {
            repository.insertAllSavedWeather(savedWeather)
        }
    }

    private fun deleteSavedWeather(savedWeather: SavedWeather) {
        viewModelScope.launch {
            repository.deleteSavedWeather(savedWeather)
        }
    }

    private fun deleteAllSavedWeather() {
        viewModelScope.launch {
            repository.deleteAllSavedWeather()
        }
    }
}