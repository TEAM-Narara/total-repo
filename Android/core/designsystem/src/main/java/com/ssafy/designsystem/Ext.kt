package com.ssafy.designsystem

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale

fun Long.formatTimestamp(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat("M월 d일 a h:mm", Locale.getDefault())
    return formatter.format(date)
}

fun formatRangeTimeStamp(start: Long, end: Long): String {
    val startDate = LocalDateTime.ofEpochSecond(start, 0, ZoneOffset.UTC)
    val endDate = LocalDateTime.ofEpochSecond(end, 0, ZoneOffset.UTC)
    val startFormatter = SimpleDateFormat(
        (if (startDate.year != endDate.year) "y년 " else "") +
                "M월 d일",
        Locale.getDefault()
    )
    val endFormatter = SimpleDateFormat(
        if(startDate.year != endDate.year || startDate.month != endDate.month || startDate.dayOfMonth != endDate.dayOfMonth) " ~ " else "" +
        if (startDate.year != endDate.year) "y년 " else "" +
        if (startDate.month != endDate.month) "M월 " else "" +
        if (startDate.dayOfMonth != endDate.dayOfMonth) "d일" else "",
        Locale.getDefault()
    )
    return "${startFormatter.format(startDate)}${endFormatter.format(startDate)}"
}