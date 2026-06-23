package com.oddzmint.weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    @SerialName("main")
    val weatherCondition: String,
    val icon: String
)