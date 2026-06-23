package com.oddzmint.weatherapp.di

import android.content.Context
import com.oddzmint.weatherapp.data.location.LocationTracker
import com.oddzmint.weatherapp.data.location.LocationTrackerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideLocationTracker(
        @ApplicationContext context: Context
    ): LocationTracker {
        return LocationTrackerImpl(context)
    }
}