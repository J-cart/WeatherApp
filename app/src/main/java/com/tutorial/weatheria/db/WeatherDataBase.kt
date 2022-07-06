package com.tutorial.weatheria.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import com.tutorial.weatheria.network_and_data_models.WeatherResponse

@Database(entities = [WeatherResponse::class,SavedWeather::class], version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDataBase: RoomDatabase(){
    abstract fun weatherDao():WeatherDao
    abstract fun savedWeatherDao():SavedWeatherDao
}