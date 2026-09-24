/*
 * Copyright (c) 2026 OddzMint.
 * Licensed under the MIT License - see LICENSE file in the project root.
 */
package com.oddzmint.weatherapp.domain.model

data class WeatherForecast(
    val day: String,
    val temperature: Int,
    val weatherType: String,
    val icon: String,
    val weatherDescription: String
)