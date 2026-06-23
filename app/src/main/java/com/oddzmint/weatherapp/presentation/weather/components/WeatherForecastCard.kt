package com.oddzmint.weatherapp.presentation.weather.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.oddzmint.weatherapp.presentation.theme.CardCornerRadius
import com.oddzmint.weatherapp.presentation.theme.CardPadding
import com.oddzmint.weatherapp.presentation.theme.CardTitle
import com.oddzmint.weatherapp.presentation.theme.ScreenPadding
import com.oddzmint.weatherapp.presentation.theme.Temperature
import com.oddzmint.weatherapp.presentation.theme.WeatherIconSize

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