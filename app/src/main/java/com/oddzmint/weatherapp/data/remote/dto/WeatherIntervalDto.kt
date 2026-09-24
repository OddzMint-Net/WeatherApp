/*
 * Copyright (c) 2026 OddzMint.
 * Licensed under the MIT License - see LICENSE file in the project root.
 */
package com.oddzmint.weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherIntervalDto(
    @SerialName("dt_txt")
    val forecastDateTime: String,
    @SerialName("main")
    val condition: WeatherConditionsDto,
    val weather: List<WeatherDto>
)