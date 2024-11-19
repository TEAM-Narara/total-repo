package com.ssafy.designsystem.values

import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Primary = Color(0xFF2E5274)
val ReversePrimary = Color(0xFFEF5350)
val DarkGray = Color(0xFF5D5D5D)
val LightDarkGray = Color(0xFFD6D6D6)
val Gray = Color(0xFFF5F5F5)
val LightGray = Color(0xFFCCCCCC)
val Pink = Color(0xFFFF76A0)
val LightPink = Color(0xFFFFE3E8)
val Yellow = Color(0xFFFFF7BD)
val SkyBlue = Color(0xFFD9E1F4)
val LightSkyBlue = Color(0xFFE5EFFF)
val Transparent = Color(0x00000000)
val LabelRed = Color(0xFFFF6B5F)
val LabelYellow = Color(0xFFFFE770)
val LabelBlue = Color(0xFF5D9CEC)

val backgroundColorList = listOf(
    Color(0xFFFCFCFC),
    Color(0xFFFFE3E8),
    Color(0xFFFFF7BD),
    Color(0xFFD9E1F4),
    Color(0xFFE5EFFF),
    Color(0xFFEAFFE5),
    Color(0xFFEEE5FF),
    Color(0xFFCCCCCC)
)

fun Color.toColorString(): String {
    return String.format(
        "#%02x%02x%02x",
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}

fun String.toColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: IllegalArgumentException) {
        Color.Black
    }
}
