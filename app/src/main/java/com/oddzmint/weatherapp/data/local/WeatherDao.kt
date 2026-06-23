package com.oddzmint.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_forecast")
    fun getWeatherForecasts(): Flow<List<WeatherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherForecasts(forecast: List<WeatherEntity>)

    @Query("DELETE FROM weather_forecast")
    suspend fun clearWeatherForecasts()
}