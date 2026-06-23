package com.oddzmint.weatherapp.domain.model

data class WeatherForecast(
    val day: String,
    val temperature: Int,
    val weatherType: String,
    val icon: String,
    val weatherDescription: String
)