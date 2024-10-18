package com.ssafy.designsystem

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatTimestamp(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat("M월 d일 a h:mm", Locale.getDefault())
    return formatter.format(date)
}