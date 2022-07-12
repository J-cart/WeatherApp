package com.tutorial.weatheria.db

import androidx.room.*
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedWeatherDao {

    @Query("SELECT* FROM saved_weather")
    fun getAllSavedWeather():Flow<List<SavedWeather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(savedWeather: SavedWeather)

    @Delete
    suspend fun deleteSavedWeather(savedWeather: SavedWeather)

    @Query("SELECT COUNT() FROM weatherResponse")
    suspend fun getSavedSize():Int

    @Query("DELETE FROM saved_weather")
    suspend fun deleteAllSavedWeather()

    @Update
    suspend fun updateSavedWeather(savedWeather: SavedWeather)
}