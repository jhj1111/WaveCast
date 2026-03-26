package com.example.wavecast.core.network.utils

import java.util.Calendar
import java.util.Date
import java.util.TimeZone

fun getApiHeaderTime(): String {
    // prep for crypto
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        .apply {
            clear()
            time = Date()
        }
    val secondsSinceEpoch = calendar.timeInMillis / 1000L
    val apiHeaderTime = "" + secondsSinceEpoch

    return apiHeaderTime
}