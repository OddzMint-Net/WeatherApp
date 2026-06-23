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
import com.oddzmint.weatherapp.presentation.weather.utils.getWeatherUiConfig

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
            val weatherUi = getWeatherUiConfig(uiState.forecast.firstOrNull()?.icon ?: "")
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

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    WeatherScreenContent(
        uiState = WeatherUiState.Success(
            forecast = listOf(
                WeatherForecast(
                    day = "Mon",
                    temperature = 22,
                    weatherType = "Cool",
                    icon = "02d",
                    weatherDescription = "Hot"
                )
            )
        )
    )
}