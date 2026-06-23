package com.oddzmint.weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherConditionsDto(
    @SerialName("temp")
    val temperature: Double
)