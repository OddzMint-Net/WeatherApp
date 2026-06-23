package com.oddzmint.weatherapp.data.local

import androidx.room.Entity

@Entity(
    tableName = "weather_forecast",
    primaryKeys = ["date", "cityName"]
)
data class WeatherEntity(
    val date:String,
    val cityName: String,
    val day: String,
    val temperature: Int,
    val weatherType: String,
    val icon: String,
    val weatherDescription: String
)