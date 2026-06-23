package com.oddzmint.weatherapp.domain.repository

import com.oddzmint.weatherapp.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getFiveDayForecast(): Flow<List<WeatherForecast>>
}