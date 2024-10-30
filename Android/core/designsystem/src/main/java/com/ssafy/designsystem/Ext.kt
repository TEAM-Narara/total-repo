package com.ssafy.designsystem

import androidx.compose.ui.graphics.Color
import com.ssafy.designsystem.values.Transparent
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar
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

    val brightness = backgroundColor.run { (red * 299 + green * 587 + blue * 114) }
    return if (brightness > 384) Color.Black else Color.White
}