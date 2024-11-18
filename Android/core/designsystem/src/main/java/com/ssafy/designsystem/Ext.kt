package com.ssafy.designsystem

import androidx.compose.ui.graphics.Color
import com.ssafy.designsystem.values.Transparent
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

val BaseDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월 d일 a h:mm")

fun Long.millisecondsToZonedDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

fun Long.secondsToZonedDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())

fun LocalDateTime.formatDefault(formatter: DateTimeFormatter = BaseDateFormatter): String =
    format(formatter)

fun Long.formatTimestamp(): String = millisecondsToZonedDateTime().formatDefault()

fun formatRangeTimeStamp(start: Long, end: Long): String {
    val startDate = start.millisecondsToZonedDateTime()
    val endDate = end.millisecondsToZonedDateTime()
    val startFormatter = DateTimeFormatter.ofPattern(
        (if (startDate.year != endDate.year) "y년 " else "") +
                "M월 d일",
        Locale.getDefault()
    )
    val endFormatter = DateTimeFormatter.ofPattern(
        (if (startDate.year != endDate.year || startDate.month != endDate.month || startDate.dayOfMonth != endDate.dayOfMonth) " ~ " else "") +
                (if (startDate.year != endDate.year) "y년 " else "") +
                (if (startDate.month != endDate.month) "M월 " else "") +
                (if (startDate.dayOfMonth != endDate.dayOfMonth) "d일" else ""),
        Locale.getDefault()
    )
    return "${startFormatter.format(startDate)}${endFormatter.format(endDate)}"
}

fun formatUnixTimeStamp(start: Long, end: Long): String {
    val startDate = Date(start)
    val endDate = Date(end)
    val cal1 = Calendar.getInstance().apply { time = startDate }
    val cal2 = Calendar.getInstance().apply { time = endDate }

    val sameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    val sameMonth = sameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())

    return buildString {
        append(dateFormat.format(startDate))
        if (sameMonth) {
            val endFormat = SimpleDateFormat("d일", Locale.getDefault())
            append(" ~ ${endFormat.format(endDate)}")
        } else if (sameYear) {
            val endFormat = SimpleDateFormat("M월 d일", Locale.getDefault())
            append(" ~ ${endFormat.format(endDate)}")
        } else {
            append(" ~ ${dateFormat.format(endDate)}")
        }
    }
}

fun getContrastingTextColor(backgroundColor: Color): Color {
    if (backgroundColor == Transparent) return Color.Black

    return if (backgroundColor.isLight) Color.Black
    else Color.White
}

val Color.isLight get() = (red * 299 + green * 587 + blue * 114) < 384