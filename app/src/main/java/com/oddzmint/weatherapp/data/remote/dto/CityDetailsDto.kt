/*
 * Copyright (c) 2026 OddzMint.
 * Licensed under the MIT License - see LICENSE file in the project root.
 */
package com.oddzmint.weatherapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CityDetailsDto(
    val name: String,
    val country: String
)