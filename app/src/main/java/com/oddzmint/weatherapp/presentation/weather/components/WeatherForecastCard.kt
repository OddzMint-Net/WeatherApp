package com.oddzmint.weatherapp.presentation.weather.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oddzmint.weatherapp.presentation.theme.CardCornerRadius
import com.oddzmint.weatherapp.presentation.theme.CardPadding
import com.oddzmint.weatherapp.presentation.theme.CardTitle
import com.oddzmint.weatherapp.presentation.theme.ScreenPadding
import com.oddzmint.weatherapp.presentation.theme.Temperature
import com.oddzmint.weatherapp.presentation.theme.WeatherIconSize
import com.oddzmint.weatherapp.ui.preview.WeatherAppPreview
import com.oddzmint.weatherapp.R
import com.oddzmint.weatherapp.domain.model.WeatherForecast

@Composable
fun WeatherForecastCard(
    day: String,
    temperature: Int,
    iconRes: Int,
    weatherDescription: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(CardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CardPadding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = day,
                    style = CardTitle
                )
                Text(
                    text = weatherDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(ScreenPadding))
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(WeatherIconSize)
                )
            }
            Text(
                text = "$temperature°",
                style = Temperature
            )
        }
    }
}

@Preview(showBackground = true, name = "Default")
@Composable
private fun WeatherForeCardPreview() {
    WeatherAppPreview {
        WeatherForecastCard(
            day = "Monday",
            temperature = 24,
            iconRes = R.drawable.ic_sun_light,
            weatherDescription = "Clear sky"
        )
    }
}

@Preview(showBackground = true, name = "All weather icons")
@Composable
private fun WeatherForecastCardAllIconsPreview() {
    WeatherAppPreview {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            WeatherForecastCard("Monday", 24, R.drawable.ic_sun_light, "Clear sky")
            WeatherForecastCard("Tuesday", 19, R.drawable.ic_thunder_light, "Scattered clouds")
            WeatherForecastCard("Wednesday", 15, R.drawable.ic_rain_light, "Light rain")
            WeatherForecastCard("Thursday", 13, R.drawable.ic_thunderstorm_light, "Thursday")
            WeatherForecastCard("Friday", -2, R.drawable.ic_snow_light, "Light snow")
            WeatherForecastCard("Saturday", 17, R.drawable.ic_moon_set_light, "Clear night")
            WeatherForecastCard("Sunday", 11, R.drawable.ic_half_moon_light, "Mist")

        }
    }
}

@Preview(showBackground = true, name = "Long description")
@Composable
private fun WeatherForecastCardLongTextPreview() {
    WeatherAppPreview {
        WeatherForecastCard(
            day = "Wednesday",
            temperature = 18,
            iconRes = R.drawable.ic_thunderstorm_light,
            weatherDescription = "Scattered thunderstorms with heavy rain in the afternoon"
        )
    }
}

@Preview(showBackground = true, name = "Extreme temperature")
@Composable
private fun WeatherForecastCardExtremePreview() {
    WeatherAppPreview {
        Column {
            WeatherForecastCard("Saturday", -12, R.drawable.ic_snow_light, "Snow")
            WeatherForecastCard("Sunday", 104, R.drawable.ic_sun_light, "Heatwave")
        }
    }
}

@Preview(showBackground = true, name = "Large font", fontScale = 1.5f)
@Composable
private fun WeatherForecastCardLargeFontPreview() {
    WeatherAppPreview {
        WeatherForecastCard(
            day = "Thursday",
            temperature = 21,
            iconRes = R.drawable.ic_cloud_light,
            weatherDescription = "Partly cloudy"
        )
    }
}

@Preview(name = "On the real background", widthDp = 360, heightDp = 400)
@Composable
private fun WeatherForecastCardOnBackgroundPreview() {
    WeatherAppPreview {
        Box {
            Image(
                painter = painterResource(R.drawable.sunny),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)){
                WeatherForecastCard("Monday",24,R.drawable.ic_sun_light,"Clear sky")
                WeatherForecastCard("Tuesday",19,R.drawable.ic_cloud_light,"Scattered clouds")
            }
        }
    }
}