package com.oddzmint.weatherapp.ui.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.oddzmint.weatherapp.ui.theme.WeatherAppTheme

@Composable
fun WeatherAppPreview(content: @Composable () -> Unit) {
    WeatherAppTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}