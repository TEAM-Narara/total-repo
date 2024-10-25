package com.ssafy.board.search.dto

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LabelOff
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.LabelRed
import com.ssafy.designsystem.values.LabelYellow
import com.ssafy.designsystem.values.Transparent
import com.ssafy.model.search.DueDate
import com.ssafy.model.search.Label

@Immutable
data class SearchAllParameters(
    val memberMap: Map<String, ParamsInfo> = mapOf(
        "담당자 없음" to ParamsInfo(
            startIcon = IconType.Vector(
                image = Icons.Default.CalendarMonth,
                backgroundColor = Gray,
            ),
            isSelected = false
        ),
        "손오공-1" to ParamsInfo(
            startIcon = IconType.Image(
                imageUrl = "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z",
            ),
            isSelected = false
        ),
        "손오공-2" to ParamsInfo(
            startIcon = IconType.Image(
                imageUrl = "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z",
            ),
            isSelected = false
        ),
        "손오공-3" to ParamsInfo(
            startIcon = IconType.Image(
                imageUrl = "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z",
            ),
            isSelected = false
        )
    ),

    val dueDateMap: Map<DueDate, ParamsInfo> = mapOf(
        DueDate.NO_DUE_DATE to ParamsInfo(
            startIcon = IconType.Vector(
                image = Icons.Default.CalendarMonth,
                backgroundColor = Gray,
            ),
            isSelected = false
        ),
        DueDate.OVERDUE to ParamsInfo(
            startIcon = IconType.Vector(
                image = Icons.Default.AccessTime,
                backgroundColor = LabelRed,
            ),
            isSelected = false
        ),
        DueDate.DUE_IN_THE_NEXT_DAY to ParamsInfo(
            startIcon = IconType.Vector(
                image = Icons.Default.AccessTime,
                backgroundColor = LabelYellow,
            ),
            isSelected = false
        ),
        DueDate.DUE_IN_THE_NEXT_WEEK to ParamsInfo(
            startIcon = IconType.Vector(
                image = Icons.Default.AccessTime,
            ),
            isSelected = false
        ),
        DueDate.DUE_IN_THE_NEXT_MONTH to ParamsInfo(
            startIcon = IconType.Vector(
                image = Icons.Default.AccessTime,
            ),
            isSelected = false
        ),
    ),

    val labelMap: Map<Label, ParamsInfo> = mapOf(
        Label(
            content = "라벨 없음",
            color = Transparent.toArgb().toLong()
        ) to ParamsInfo(
            startIcon = IconType.Vector(
                image = Icons.AutoMirrored.Outlined.LabelOff,
                backgroundColor = Gray,
            ),
            isSelected = false
        ),
        Label(
            content = "라벨1",
            color = Color.Green.toArgb().toLong()
        ) to ParamsInfo(
            startIcon = IconType.None,
            isSelected = false
        ),
        Label(
            content = "라벨2",
            color = Color.Red.toArgb().toLong()
        ) to ParamsInfo(
            startIcon = IconType.None,
            isSelected = false
        ),
        Label(
            content = "라벨3",
            color = Color.Blue.toArgb().toLong()
        ) to ParamsInfo(
            startIcon = IconType.None,
            isSelected = false
        ),
        Label(
            content = "라벨4",
            color = Color.Yellow.toArgb().toLong()
        ) to ParamsInfo(
            startIcon = IconType.None,
            isSelected = false
        ),
    )

) {
    var searchedText: String = ""
}
