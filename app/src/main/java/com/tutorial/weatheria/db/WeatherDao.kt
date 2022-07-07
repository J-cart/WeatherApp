package com.tutorial.weatheria.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tutorial.weatheria.network_and_data_models.WeatherResponse

@Dao
interface WeatherDao {
    @Query("SELECT* FROM weatherResponse")
    suspend fun getWeatherResponse(): WeatherResponse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)

    @Query("DELETE FROM weatherResponse")
    suspend fun deleteAll()

    @Query("SELECT COUNT() FROM weatherResponse")
    suspend fun getSize():Int
}