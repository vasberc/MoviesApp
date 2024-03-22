package com.vasberc.data.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


const val DATE_NO_TIME_US_PATTERN = "yyyy-MM-dd"

fun localDateTimeToStringWithDesiredPattern(dateTime: LocalDateTime, pattern: String): String? {
    return try {
        dateTime.format(DateTimeFormatter.ofPattern(pattern))
    } catch (e: Exception) {
        null
    }
}