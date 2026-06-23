package com.oddzmint.weatherapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CityDetailsDto(
    val name: String,
    val country: String
)