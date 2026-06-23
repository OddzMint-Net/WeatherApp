package com.oddzmint.weatherapp.domain.utils

fun Double.toWeatherDescription(): String {
    return when {
        this < 15 -> "Cold"
        this < 20 -> "Cool"
        this <= 26 -> "Warm"
        else -> "Hot"
    }
}