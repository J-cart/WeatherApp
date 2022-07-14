package com.tutorial.weatheria.db

import androidx.room.*
import com.tutorial.weatheria.network_and_data_models.Location
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import kotlinx.coroutines.flow.Flow
import java.io.StringBufferInputStream

@Dao
interface SavedWeatherDao {

    @Query("SELECT* FROM saved_weather")
    fun getAllSavedWeather():Flow<List<SavedWeather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(savedWeather: SavedWeather)

    @Delete
    suspend fun deleteSavedWeather(savedWeather: SavedWeather)

    @Query("SELECT COUNT() FROM saved_weather")
    suspend fun getSavedSize():Int

    @Query("DELETE FROM saved_weather")
    suspend fun deleteAllSavedWeather()

    @Query("SELECT COUNT() FROM saved_weather WHERE location =:name" )
    suspend fun checkIfExist(name:Location):Int

    @Update
    suspend fun updateSavedWeather(savedWeather: SavedWeather)
}