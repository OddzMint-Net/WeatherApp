package com.oddzmint.weatherapp

import android.location.Location
import com.google.common.truth.Truth.assertThat
import com.oddzmint.weatherapp.data.local.WeatherDao
import com.oddzmint.weatherapp.data.local.WeatherEntity
import com.oddzmint.weatherapp.data.location.LocationTracker
import com.oddzmint.weatherapp.data.remote.api.WeatherApi
import com.oddzmint.weatherapp.data.remote.dto.WeatherIntervalDto
import com.oddzmint.weatherapp.data.remote.dto.WeatherForecastResponseDto
import com.oddzmint.weatherapp.data.remote.dto.WeatherConditionsDto
import com.oddzmint.weatherapp.data.remote.dto.WeatherDto
import com.oddzmint.weatherapp.data.repository.WeatherRepositoryImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {

    private val weatherApi: WeatherApi = mockk()
    private val locationTracker: LocationTracker = mockk()
    private val weatherDao: WeatherDao = mockk()

    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        repository = WeatherRepositoryImpl(
            weatherApi = weatherApi,
            locationTracker = locationTracker,
            weatherDao = weatherDao
        )
    }

    @Test
    fun `given api success, returns fresh forecast`() = runTest {
        coEvery { locationTracker.getCurrentLocation() } returns mockLocation()
        coEvery { weatherApi.getFiveDayForecast(any(), any(), any(), any()) } returns mockForecastResponse()
        coEvery { weatherDao.clearWeatherForecasts() } just Runs
        coEvery { weatherDao.insertWeatherForecasts(any()) } just Runs

        val result = repository.getFiveDayForecast()

        assertThat(result).isNotEmpty()
        coVerify { weatherDao.clearWeatherForecasts() }
        coVerify { weatherDao.insertWeatherForecasts(any()) }
    }

    @Test
    fun `given api success, caches data to room`() = runTest {
        coEvery { locationTracker.getCurrentLocation() } returns mockLocation()
        coEvery { weatherApi.getFiveDayForecast(any(), any(), any(), any()) } returns mockForecastResponse()
        coEvery { weatherDao.clearWeatherForecasts() } just Runs
        coEvery { weatherDao.insertWeatherForecasts(any()) } just Runs

        repository.getFiveDayForecast()

        coVerify(exactly = 1) { weatherDao.clearWeatherForecasts() }
        coVerify(exactly = 1) { weatherDao.insertWeatherForecasts(any()) }
    }

    @Test
    fun `given api failure and cache exists, returns cached data`() = runTest {
        // Arrange
        coEvery { locationTracker.getCurrentLocation() } returns mockLocation()
        coEvery { weatherApi.getFiveDayForecast(any(), any(), any(), any()) } throws Exception("No internet")
        coEvery { weatherDao.getWeatherForecasts() } returns mockCachedEntities()

        val result = repository.getFiveDayForecast()

        assertThat(result).isNotEmpty()
        assertThat(result.first().day).isEqualTo("Mon")
    }

    @Test
    fun `given api failure and no cache, throws exception`() = runTest {
        coEvery { locationTracker.getCurrentLocation() } returns mockLocation()
        coEvery { weatherApi.getFiveDayForecast(any(), any(), any(), any()) } throws Exception("No internet")
        coEvery { weatherDao.getWeatherForecasts() } returns emptyList()

        assertThrows(Exception::class.java) {
            runBlocking { repository.getFiveDayForecast() }
        }
    }

    @Test
    fun `given null location, uses default coordinates`() = runTest {
        coEvery { locationTracker.getCurrentLocation() } returns null
        coEvery { weatherApi.getFiveDayForecast(
            latitude = 0.0,
            longitude = 0.0,
            apiKey = any(),
            units = any()
        ) } returns mockForecastResponse()
        coEvery { weatherDao.clearWeatherForecasts() } just Runs
        coEvery { weatherDao.insertWeatherForecasts(any()) } just Runs

        val result = repository.getFiveDayForecast()

        assertThat(result).isNotEmpty()
        coVerify {
            weatherApi.getFiveDayForecast(
                latitude = 0.0,
                longitude = 0.0,
                apiKey = any(),
                units = any()
            )
        }
    }


    private fun mockLocation(): Location {
        val location = mockk<Location>()
        every { location.latitude } returns -33.9249
        every { location.longitude } returns 18.4241
        return location
    }

    private fun mockForecastResponse() = WeatherForecastResponseDto(
        list = listOf(
            WeatherIntervalDto(
                dt_txt = "2026-05-04 12:00:00",
                main = WeatherConditionsDto(temp = 20.0),
                weather = listOf(WeatherDto(weatherCondition = "Clear", icon = "01d"))
            ),
            WeatherIntervalDto(
                dt_txt = "2026-06-05 06:00:00",
                main = WeatherConditionsDto(temp = 18.0),
                weather = listOf(WeatherDto(weatherCondition = "Clouds", icon = "02d"))
            ),
            WeatherIntervalDto(
                dt_txt = "2026-05-05 09:00:00",
                main = WeatherConditionsDto(temp = 22.0),
                weather = listOf(WeatherDto(weatherCondition = "Clouds", icon = "02d"))
            ),
            WeatherIntervalDto(
                dt_txt = "2026-06-06 12:00:00",
                main = WeatherConditionsDto(temp = 25.0),
                weather = listOf(WeatherDto(weatherCondition = "Clear", icon = "01d"))
            ),
            WeatherIntervalDto(
                dt_txt = "2026-05-07 12:00:00",
                main = WeatherConditionsDto(temp = 19.0),
                weather = listOf(WeatherDto(weatherCondition = "Rain", icon = "10d"))
            ),
            WeatherIntervalDto(
                dt_txt = "2026-05-08 12:00:00",
                main = WeatherConditionsDto(temp = 17.0),
                weather = listOf(WeatherDto(weatherCondition = "Rain", icon = "10d"))
            ),

            WeatherIntervalDto(
                dt_txt = "2026-05-09 12:00:00",
                main = WeatherConditionsDto(temp = 21.0),
                weather = listOf(WeatherDto(weatherCondition = "Clear", icon = "01d"))
            )
        )
    )

    private fun mockCachedEntities() = listOf(
        WeatherEntity(
            day = "Mon",
            temperature = 22,
            weatherType = "Clear",
            icon = "01d"
        )
    )
}