package com.oddzmint.weatherapp.presentation.weather

import com.oddzmint.weatherapp.domain.model.WeatherForecast
import com.oddzmint.weatherapp.presentation.weather.utils.TimeOfDay
import com.oddzmint.weatherapp.presentation.weather.utils.getTimeOfDay

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data object PermissionDenied : WeatherUiState
    data class Success(
        val forecast: List<WeatherForecast>,
        val timeOfDay: TimeOfDay = getTimeOfDay()
    ) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}