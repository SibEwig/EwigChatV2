package com.sibewig.ewigchatv2.presentation.extensions

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.isToday(): Boolean {
    val zone = ZoneId.systemDefault()

    val messageDate = Instant.ofEpochMilli(this)
        .atZone(zone)
        .toLocalDate()

    val today = LocalDate.now(zone)

    return messageDate == today
}

fun Long.toMessageTime(): String {
    val zone = ZoneId.systemDefault()

    val dateTime = Instant.ofEpochMilli(this)
        .atZone(zone)
        .toLocalDateTime()

    return if (isToday()) {
        dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } else {
        dateTime.format(DateTimeFormatter.ofPattern("dd.MM HH:mm"))
    }
}