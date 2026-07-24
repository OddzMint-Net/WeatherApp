package com.oddzmint.weatherapp.data.repository

import android.util.Log
import com.oddzmint.weatherapp.data.remote.api.WeatherApi
import com.oddzmint.weatherapp.domain.model.WeatherForecast
import com.oddzmint.weatherapp.domain.repository.WeatherRepository
import com.oddzmint.weatherapp.BuildConfig
import com.oddzmint.weatherapp.data.local.WeatherDao
import com.oddzmint.weatherapp.data.local.WeatherEntity
import com.oddzmint.weatherapp.data.location.LocationTracker
import com.oddzmint.weatherapp.data.remote.dto.WeatherForecastResponseDto
import com.oddzmint.weatherapp.data.remote.dto.WeatherIntervalDto
import com.oddzmint.weatherapp.domain.utils.formatDay
import com.oddzmint.weatherapp.domain.utils.toWeatherDescription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val locationTracker: LocationTracker,
    private val weatherDao: WeatherDao
) : WeatherRepository {

    override fun getFiveDayForecast(): Flow<List<WeatherForecast>> {
        return weatherDao.getWeatherForecasts()
            .onStart { refreshFromNetwork() }
            .map { entities -> entities.map { it.toDomain() } }
    }

    private suspend fun refreshFromNetwork() {
        try {
            val location = locationTracker.getCurrentLocation()
            val response = weatherApi.getFiveDayForecast(
                latitude = location?.latitude ?: 0.0,
                longitude = location?.longitude ?: 0.0,
                apiKey = BuildConfig.WEATHER_API_KEY.trim()
            )
            weatherDao.clearWeatherForecasts()
            weatherDao.insertWeatherForecasts(response.toEntities())
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Network fresh failed:${e.message}")
        }
    }

    private fun WeatherForecastResponseDto.toEntities(): List<WeatherEntity> {
        return intervals
            .groupBy { it.forecastDateTime.substring(0, 10) }
            .entries
            .take(5)
            .map { (date, intervals) ->
                WeatherEntity(
                    date = date,
                    cityName = city.name,
                    day = formatDay(intervals.first().forecastDateTime),
                    temperature = intervals.maxOf {
                        it.condition.temperature
                    }.toInt(),
                    weatherType = intervals.getMostFrequentWeatherType(),
                    icon = intervals.getMostFrequentIcon(),
                    weatherDescription = intervals.maxOf { it.condition.temperature }.toWeatherDescription()
                )
            }
    }

    private fun List<WeatherIntervalDto>.getMostFrequentWeatherType(): String {
        return groupBy { it.weather.firstOrNull()?.weatherCondition ?: "" }
            .maxByOrNull { it.value.size }
            ?.key ?: ""
    }

    private fun List<WeatherIntervalDto>.getMostFrequentIcon(): String {
        return groupBy { it.weather.firstOrNull()?.icon ?: "" }
            .maxByOrNull { it.value.size }
            ?.key ?: ""
    }


    fun WeatherEntity.toDomain(): WeatherForecast {
        return WeatherForecast(
            day = day,
            temperature = temperature,
            weatherType = weatherType,
            icon = icon,
            weatherDescription = weatherDescription
        )
    }
}