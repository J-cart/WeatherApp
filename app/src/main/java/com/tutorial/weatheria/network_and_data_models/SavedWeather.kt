package com.tutorial.weatheria.network_and_data_models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_weather")
data class SavedWeather(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val location: Location? = null,
    val current: Current? = null
)