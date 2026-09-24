/*
 * Copyright (c) 2026 OddzMint.
 * Licensed under the MIT License - see LICENSE file in the project root.
 */
package com.oddzmint.weatherapp.presentation.weather

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oddzmint.weatherapp.R
import com.oddzmint.weatherapp.domain.model.WeatherForecast
import com.oddzmint.weatherapp.presentation.theme.CardSpacing
import com.oddzmint.weatherapp.presentation.theme.ScreenPadding
import com.oddzmint.weatherapp.presentation.theme.ScreenTitle
import com.oddzmint.weatherapp.presentation.weather.components.WeatherForecastCard
import com.oddzmint.weatherapp.presentation.weather.utils.TimeOfDay
import com.oddzmint.weatherapp.presentation.weather.utils.getWeatherUiConfig
import com.oddzmint.weatherapp.ui.preview.WeatherAppPreview
import com.oddzmint.weatherapp.ui.theme.WeatherAppTheme

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    WeatherScreenContent(uiState = uiState)
}

@Composable
fun WeatherScreenContent(uiState: WeatherUiState) {
    val context = LocalContext.current

    when (uiState) {
        is WeatherUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.testTag(""))
            }
        }

        is WeatherUiState.PermissionDenied -> {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Location Permission Required") },
                text = { Text("This app needs location permission to show weather for your area") },
                confirmButton = {
                    TextButton(onClick = {
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }) {
                        Text("Allow")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                            (context as? Activity)?.finish()
                        }) {
                        Text("Deny")
                    }
                }
            )
        }

        is WeatherUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.message}", color = Color.Red)
            }
        }

        is WeatherUiState.Success -> {
            val weatherUi = getWeatherUiConfig(
                iconCode = uiState.forecast.firstOrNull()?.icon ?: "",
                timeOfDay = uiState.timeOfDay
            )
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(weatherUi.backgroundRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = stringResource(R.string.description),
                        style = ScreenTitle,
                        color = Color.White,
                        modifier = Modifier.padding(
                            horizontal = ScreenPadding
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = Color.White
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(horizontal = ScreenPadding),
                        verticalArrangement = Arrangement.spacedBy(
                            CardSpacing
                        )
                    ) {
                        items(uiState.forecast, key = { it.day }) { forecast ->
                            val forecastUi = getWeatherUiConfig(
                                iconCode = forecast.icon,
                                timeOfDay = uiState.timeOfDay
                            )
                            WeatherForecastCard(
                                day = forecast.day,
                                temperature = forecast.temperature,
                                iconRes = forecastUi.iconRes,
                                weatherDescription = forecast.weatherDescription
                            )
                        }
                    }
                }
            }
        }
    }
}

private val mixedWeek = listOf(
    WeatherForecast("Monday", 24, "Clear", "01d", "Clear sky"),
    WeatherForecast("Tuesday", 19, "Clouds", "03d", "Scattered clouds"),
    WeatherForecast("Wednesday", 15, "Rain", "10d", "Light rain"),
    WeatherForecast("Thursday", 13, "Thunderstorm", "11d", "Thunderstorm"),
    WeatherForecast("Friday", -2, "Snow", "13d", "Light snow")
)

private fun weekOf(icon: String, description: String) = listOf(
    "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
).mapIndexed { index, day ->
    WeatherForecast(day, 24 - index * 2, "", icon, description)
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun WeatherLoadingPreview() {
    WeatherAppPreview {
        WeatherScreenContent(
            WeatherUiState.Loading
        )
    }
}

@Preview(showBackground = true, name = "Permission denied")
@Composable
private fun WeatherPermissionDeniedPreview() {
    WeatherAppPreview {
        WeatherScreenContent(WeatherUiState.PermissionDenied)
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun WeatherErrorPreview() {
    WeatherAppPreview {
        WeatherScreenContent(WeatherUiState.Error("Unable to resolve host api.openweather.org"))
    }
}

@Preview(showBackground = true, name = "Sunny morning",device = "spec:width=1080px,height=1920px,dpi=440")
@Composable
private fun WeatherSunnyMorningPreview() {
    WeatherAppPreview {
        WeatherScreenContent(
            WeatherUiState.Success(weekOf("01d", "Clear sky"), TimeOfDay.MORNING)
        )
    }
}

@Preview(showBackground = true, name = "Clear night",device = "spec:width=1080px,height=1920px,dpi=440")
@Composable
private fun WeatherClearNightPreview() {
    WeatherAppPreview {
        WeatherScreenContent(
            WeatherUiState.Success(weekOf("01d", "Clear sky"), TimeOfDay.NIGHT)
        )
    }
}

@Preview(showBackground = true, name = "Rainy afternoon",device = "spec:width=1080px,height=1920px,dpi=440")
@Composable
private fun WeatherRainyPreview() {
    WeatherAppPreview {
        WeatherScreenContent(
            WeatherUiState.Success(weekOf("10d", "Light rain"), TimeOfDay.AFTERNOON)
        )
    }
}

@Preview(showBackground = true, name = "Mixed week",device = "spec:width=1080px,height=1920px,dpi=440")
@Composable
private fun WeatherMixedWeekPreview() {
    WeatherAppPreview {
        WeatherScreenContent(WeatherUiState.Success(mixedWeek, TimeOfDay.AFTERNOON))
    }
}