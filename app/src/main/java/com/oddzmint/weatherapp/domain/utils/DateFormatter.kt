/*
 * Copyright (c) 2026 OddzMint.
 * Licensed under the MIT License - see LICENSE file in the project root.
 */
package com.oddzmint.weatherapp.domain.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDay(
    dateText: String
): String {
    val inputFormat = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        Locale.getDefault()
    )
    val outputFormat = SimpleDateFormat(
        "EEE",
        Locale.getDefault()
    )
    val date = inputFormat.parse(dateText)
    return outputFormat.format(date ?: "")
}