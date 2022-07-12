package com.tutorial.weatheria.arch

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.db.WeatherDataBase
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import com.tutorial.weatheria.network_and_data_models.SearchLocationResponse
import com.tutorial.weatheria.network_and_data_models.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: MainRepository,
    private val db: WeatherDataBase
) : ViewModel() {
    val weatherForecast = MutableLiveData<Resource<WeatherResponse>>()
    val searchLocationResult = MutableLiveData<Resource<SearchLocationResponse>>()
    val searchLocationWeatherResult = MutableLiveData<Resource<WeatherResponse>>()

    //////////////////////REGION FOR STILL CHECKING OUT THE CACHING//////////////////////////////
    val frmDab = MutableLiveData<WeatherResponse>()
    val situFrmDab = MutableLiveData<Resource<WeatherResponse>>()
    val savedWeatherResult = MutableLiveData<List<SavedWeather>>()
    val onlineSavedWeatherResultTest = MutableLiveData<Resource<List<SavedWeather>>>()
    val offLineSavedWeatherResultTest = MutableLiveData<Resource<List<SavedWeather>>>()
    val list = mutableListOf<SavedWeather>()

    init {

        viewModelScope.launch {
            getAllSavedWeather().collect {
                list.addAll(it)
            }
        }
    }


    fun reportDbSitu() {
        viewModelScope.launch {
            val isDbEmpty = db.weatherDao().getSize() < 1
            when {
                isDbEmpty -> {
                    situFrmDab.value = Resource.Failure("Db is empty")
                }
                else -> {
                    situFrmDab.value = Resource.Successful(repository.getAllWeatherFromDb())
                }

            }
        }
    }

    //THIS IS FOR THE NORMAL REQUEST WITHOUT CACHING!!...sorry theres caching now
    fun updateWeather(location: String) {
        weatherForecast.value = Resource.Loading()
        viewModelScope.launch {
            when (val forecast = repository.getWeatherForecast(location = location, 7)) {
                is Resource.Successful -> {
                    forecast.data?.let {
                        weatherForecast.value = Resource.Successful(it)
                        db.withTransaction {
                            deleteAllWeather()
                            Log.d("dbtransaction", "deleting weather")
                            insertAllWeather(it)
                            Log.d("dbtransaction", "inserting weather")
                        }
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

    //REGULAR FORECAST
    private fun getAllWeatherFromDb() {
        viewModelScope.launch {
            repository.getAllWeatherFromDb()
        }
    }

    private fun insertAllWeather(weatherResponse: WeatherResponse) {
        viewModelScope.launch {
            repository.insertAllWeather(weatherResponse)
        }
    }

    private fun deleteAllWeather() {
        viewModelScope.launch {
            repository.deleteAllWeather()
        }
    }


    //SAVED WEATHER
    private suspend fun getAllSavedWeather(): Flow<List<SavedWeather>> =
        repository.getAllSavedWeather()

    fun insertSavedWeather(savedWeather: SavedWeather) {
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

    fun doOnlineSavedOperation(list: List<SavedWeather>) {
        onlineSavedWeatherResultTest.value = Resource.Loading()
        viewModelScope.launch {
            val onlineWeatherList = mutableListOf<SavedWeather>()
            db.withTransaction {

                list.forEach { savedWeather ->
                    when (val getWeather =
                        repository.getWeatherForSearchedLocation(savedWeather.location!!.name, 4)) {
                        is Resource.Successful -> {
                            val location = getWeather.data?.location
                            val current = getWeather.data?.current
                            val weather = savedWeather.copy(
                                location = location,
                                current = current
                            )
                            onlineWeatherList.add(weather)
                            db.savedWeatherDao().updateSavedWeather(weather)
                        }
                        is Resource.Failure -> {
                            Log.d("savedPRocess", "${getWeather.msg}")
                        }

                    }

                }
                Log.d("savedPRocess", "$onlineWeatherList")

            }
            onlineSavedWeatherResultTest.value = Resource.Successful(onlineWeatherList)
        }

    }

    fun reportSavedDbSitu() {
        offLineSavedWeatherResultTest.value = Resource.Loading()
        viewModelScope.launch {
            val isSavedEmpty = db.savedWeatherDao().getSavedSize() < 1
            when {
                isSavedEmpty -> offLineSavedWeatherResultTest.value =
                    Resource.Failure("List is bloody empty")
                else -> {
                    getAllSavedWeather().collect {
                        offLineSavedWeatherResultTest.value = Resource.Successful(it)
                    }
                }
            }
        }
    }
}