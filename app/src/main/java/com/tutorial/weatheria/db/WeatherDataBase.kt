package com.tutorial.weatheria.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tutorial.weatheria.networkmodels.WeatherResponse

@Database(entities = [WeatherResponse::class], version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDataBase: RoomDatabase(){
    abstract fun weatherDao():WeatherDao
}