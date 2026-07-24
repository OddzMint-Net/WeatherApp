package com.oddzmint.weatherapp

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.oddzmint.weatherapp.domain.model.WeatherForecast
import com.oddzmint.weatherapp.domain.repository.WeatherRepository
import com.oddzmint.weatherapp.presentation.weather.WeatherUiState
import com.oddzmint.weatherapp.presentation.weather.WeatherViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class WeatherViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository: WeatherRepository = mockk()
    private lateinit var weatherViewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        weatherViewModel = WeatherViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given permission denied, when onLocationPermissionResult is called with false, then emits PermissionDenied state`() =
        runTest {
            weatherViewModel.uiState.test {
                weatherViewModel.onLocationPermissionResult(false)
                skipItems(1)
                val state = awaitItem()
                assertThat(state).isEqualTo(WeatherUiState.PermissionDenied)
            }
        }

    @Test
    fun `given permission granted, when onLocationPermissionResult is called with true, then emits Success state`() =
        runTest {
            every { repository.getFiveDayForecast() } returns flowOf(listOf(mockWeatherForecast()))
            weatherViewModel.uiState.test {
                weatherViewModel.onLocationPermissionResult(true)
                skipItems(1)

                val state = awaitItem()
                assertThat(state).isInstanceOf(WeatherUiState.Success::class.java)
            }

        }

    private fun mockWeatherForecast() = WeatherForecast(
        day = "2026/06/24",
        temperature = 23,
        weatherType = "Hot",
        icon = "02d",
        weatherDescription = "Hot"
    )

    @Test
    fun `given permission granted, when onLocationPermissionResult is called with true,then emits Error`() = runTest {
        every { repository.getFiveDayForecast() } returns flow {
            throw IOException("Network Error")
        }
        weatherViewModel.uiState.test {
            weatherViewModel.onLocationPermissionResult(true)
            skipItems(1)

            val state = awaitItem()
            assertThat(state).isInstanceOf(WeatherUiState.Error::class.java)
        }
    }
}