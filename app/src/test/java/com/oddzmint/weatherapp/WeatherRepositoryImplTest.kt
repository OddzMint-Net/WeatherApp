package com.oddzmint.weatherapp

import android.location.Location
import com.google.common.truth.Truth.assertThat
import com.oddzmint.weatherapp.data.local.WeatherDao
import com.oddzmint.weatherapp.data.local.WeatherEntity
import com.oddzmint.weatherapp.data.location.LocationTracker
import com.oddzmint.weatherapp.data.remote.api.WeatherApi
import com.oddzmint.weatherapp.data.remote.dto.CityDetailsDto
import com.oddzmint.weatherapp.data.remote.dto.WeatherConditionsDto
import com.oddzmint.weatherapp.data.remote.dto.WeatherDto
import com.oddzmint.weatherapp.data.remote.dto.WeatherForecastResponseDto
import com.oddzmint.weatherapp.data.remote.dto.WeatherIntervalDto
import com.oddzmint.weatherapp.data.repository.WeatherRepositoryImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {
    private val weatherApi: WeatherApi = mockk()
    private val locationTracker: LocationTracker = mockk()
    private val weatherDao: WeatherDao = mockk()
    private lateinit var weatherRepository: WeatherRepositoryImpl

    @Before
    fun setup() {
        weatherRepository = WeatherRepositoryImpl(weatherApi, locationTracker, weatherDao)
    }

    @Test
    fun `given valid location and api response, when getFiveDayForecast is called, then emits mapped domain models`() =
        runTest {
            coEvery { locationTracker.getCurrentLocation() } returns mockLocation()
            coEvery { weatherApi.getFiveDayForecast(any(), any(), any()) } returns mockForecastResponse()
            coEvery { weatherDao.clearWeatherForecasts() } just Runs
            coEvery { weatherDao.insertWeatherForecasts(any()) } just Runs

            every { weatherDao.getWeatherForecasts() } returns flowOf(listOf(mockWeatherEntity()))

            val result = weatherRepository.getFiveDayForecast().first()
            assertThat(result).isNotEmpty()
            assertThat(result.first().day).isEqualTo("Mon")
            assertThat(result.last().temperature).isEqualTo(2)
        }

    private fun mockLocation() = Location("provider").apply {
        latitude = -33.9249
        longitude = 18.4241
    }

    private fun mockWeatherEntity() = WeatherEntity(
        date = "2026/06/24",
        cityName = "Cape Town",
        day = "Mon",
        temperature = 2,
        weatherType = "Clear",
        icon = "Hot Icon",
        weatherDescription = "Hot"
    )

    private fun mockForecastResponse() = WeatherForecastResponseDto(
        intervals = listOf(
            WeatherIntervalDto(
                forecastDateTime = "2026-06-24 12:00:00",
                condition = WeatherConditionsDto(temperature = 20.00),
                weather = listOf(WeatherDto(weatherCondition = "Warm", icon = "01d"))
            )
        ),
        city = CityDetailsDto(name = "CapeTown", country = "South Africa")
    )

    @Test
    fun `given null location, when giveFiveDayForecast is called, then defaults to 0,0 coordinates`() = runTest {
        coEvery { locationTracker.getCurrentLocation() } returns null
        coEvery { weatherApi.getFiveDayForecast(any(), any(), any()) } returns mockForecastResponse()
        coEvery { weatherDao.clearWeatherForecasts() } just Runs
        coEvery { weatherDao.insertWeatherForecasts(any()) } just Runs
        every { weatherDao.getWeatherForecasts() } returns flowOf(listOf(mockWeatherEntity()))

        weatherRepository.getFiveDayForecast().first()
        coVerify { weatherApi.getFiveDayForecast(latitude = 0.0, longitude = 0.0, apiKey = any()) }
    }

    @Test
    fun `given network failure, when givenFiveDayForecast is called, then falls back to cached data`() = runTest {
        coEvery { locationTracker.getCurrentLocation() } returns null
        coEvery { weatherApi.getFiveDayForecast(any(),any(),any()) } throws Exception("Network error")
        coEvery { weatherDao.clearWeatherForecasts() } just Runs
        coEvery { weatherDao.insertWeatherForecasts(any()) } just Runs
        every { weatherDao.getWeatherForecasts() } returns flowOf(listOf(mockWeatherEntity()))

        val result = weatherRepository.getFiveDayForecast().first()
        assertThat(result).isNotEmpty()
    }

    @Test
    fun `given network failure and empty cache, when getFiveDayForecast is called, then emits empty list`() = runTest {
        coEvery { locationTracker.getCurrentLocation() } returns null
        coEvery { weatherApi.getFiveDayForecast(any(),any(),any()) } throws Exception("Network error")
        coEvery { weatherDao.clearWeatherForecasts() } just Runs
        coEvery { weatherDao.insertWeatherForecasts(any()) } just Runs
        every { weatherDao.getWeatherForecasts() } returns flowOf(emptyList())

        val result = weatherRepository.getFiveDayForecast().first()
        assertThat(result).isEmpty()
    }
}