package com.oddzmint.weatherapp.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oddzmint.weatherapp.domain.repository.WeatherRepository
import com.oddzmint.weatherapp.presentation.weather.utils.TimeOfDay
import com.oddzmint.weatherapp.presentation.weather.utils.getTimeOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val permissionState = MutableSharedFlow<WeatherUiState>(replay = 1)
    val uiState: StateFlow<WeatherUiState> = merge(
        refreshTrigger.flatMapLatest {
            combine(
                repository.getFiveDayForecast(),
                observeTimeOfDay()
            ) { forecast, timeOfDay ->
                WeatherUiState.Success(
                    forecast = forecast,
                    timeOfDay = timeOfDay
                ) as WeatherUiState

            }.catch { e ->
                emit(WeatherUiState.Error(e.message ?: "Unknown error"))
            }
        },
        permissionState
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WeatherUiState.Loading
        )

    companion object {
        private const val TIME_OF_DAY_REFRESH_INTERVAL = 60_000L
    }

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) {
            refreshTrigger.tryEmit(Unit)
        } else {
            permissionState.tryEmit(WeatherUiState.PermissionDenied)
        }
    }

    private fun observeTimeOfDay(): Flow<TimeOfDay> = flow {
        while (true) {
            emit(getTimeOfDay())
            delay(TIME_OF_DAY_REFRESH_INTERVAL.milliseconds)
        }
    }.distinctUntilChanged()
}