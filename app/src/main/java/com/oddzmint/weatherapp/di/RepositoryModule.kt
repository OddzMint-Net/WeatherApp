package com.oddzmint.weatherapp.di

import com.oddzmint.weatherapp.data.local.WeatherDao
import com.oddzmint.weatherapp.data.location.LocationTracker
import com.oddzmint.weatherapp.data.remote.api.WeatherApi
import com.oddzmint.weatherapp.data.repository.WeatherRepositoryImpl
import com.oddzmint.weatherapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApi: WeatherApi,
        locationTracker: LocationTracker,
        weatherDao: WeatherDao
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            weatherApi = weatherApi,
            locationTracker = locationTracker,
            weatherDao = weatherDao
        )
    }
}