package com.oddzmint.weatherapp.presentation.weather.utils

import com.oddzmint.weatherapp.R

enum class TimeOfDay { MORNING, AFTERNOON, EVENING, NIGHT }

fun getTimeOfDay(): TimeOfDay {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 6..11 -> TimeOfDay.MORNING
        in 12..17 -> TimeOfDay.AFTERNOON
        in 18..20 -> TimeOfDay.EVENING
        else -> TimeOfDay.NIGHT
    }
}

fun getWeatherUiConfig(
    iconCode: String,
    timeOfDay: TimeOfDay = getTimeOfDay()
): WeatherUiConfig {

    val normalizedCode = iconCode.replace("n", "d")

    return when (normalizedCode) {
        "01d" -> when (timeOfDay) {
            TimeOfDay.NIGHT, TimeOfDay.EVENING -> WeatherUiConfig(
                backgroundRes = R.drawable.cloudy,
                iconRes = R.drawable.ic_moon_set_light
            )

            else -> WeatherUiConfig(
                backgroundRes = R.drawable.sunny,
                iconRes = R.drawable.ic_sun_light
            )
        }

        "02d", "03d", "04d" -> when (timeOfDay) {
            TimeOfDay.NIGHT, TimeOfDay.EVENING -> WeatherUiConfig(
                backgroundRes = R.drawable.cloudy,
                iconRes = R.drawable.ic_moon_set_light
            )

            else -> WeatherUiConfig(
                backgroundRes = R.drawable.cloudy,
                iconRes = R.drawable.ic_cloud_light
            )
        }
        //rain
        "09d", "10d" -> WeatherUiConfig(
            backgroundRes = R.drawable.rainy,
            iconRes = R.drawable.ic_rain_light
        )

        //thunderstorm
        "11d" -> WeatherUiConfig(
            backgroundRes = R.drawable.rainy,
            iconRes = R.drawable.ic_thunderstorm_light
        )

        //snow
        "13d", "13n" -> WeatherUiConfig(
            backgroundRes = R.drawable.cloudy,
            iconRes = R.drawable.ic_snow_light
        )

        //mist/fog
        "50d" -> WeatherUiConfig(
            backgroundRes = R.drawable.cloudy,
            iconRes = R.drawable.ic_half_moon_light
        )

        else -> WeatherUiConfig(
            backgroundRes = R.drawable.cloudy,
            iconRes = R.drawable.ic_sun_light
        )
    }
}