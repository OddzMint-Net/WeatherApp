package com.oddzmint.weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherForecastResponseDto(
    @SerialName("list")
    val intervals: List<WeatherIntervalDto>,
    val city: CityDetailsDto
)