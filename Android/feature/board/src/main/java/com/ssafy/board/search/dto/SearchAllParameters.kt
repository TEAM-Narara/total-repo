package com.ssafy.board.search.dto

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LabelOff
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.toArgb
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.LabelRed
import com.ssafy.designsystem.values.LabelYellow
import com.ssafy.designsystem.values.Transparent
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.search.DueDate
import com.ssafy.model.search.Label
import com.ssafy.model.user.User

@Immutable
data class SearchAllParameters(
    val noMember: Pair<String, ParamsInfo> = "담당자 없음" to ParamsInfo(
        startIcon = IconType.Vector(
            image = Icons.Default.Person,
            backgroundColor = Gray,
        ),
        isSelected = false
    ),
    val memberMap: Map<User, ParamsInfo> = mapOf(),

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

    val noLabel: Pair<Label, ParamsInfo> = Label(
        id = 0,
        content = "라벨 없음",
        color = Transparent.toArgb().toLong()
    ) to ParamsInfo(
        startIcon = IconType.Vector(
            image = Icons.AutoMirrored.Outlined.LabelOff,
            backgroundColor = Gray,
        ),
        isSelected = false
    ),

    val labelMap: Map<Label, ParamsInfo> = mapOf(),

    var searchedText: String = ""
)

fun User.toParamsInfo() = ParamsInfo(
    startIcon = IconType.Image(imageUrl = profileImgUrl),
    isSelected = false
)

fun LabelDTO.toLabel() = Label(
    id = labelId,
    content = labelName,
    color = labelColor
)

fun LabelDTO.toParamsInfo() = ParamsInfo(
    startIcon = IconType.None,
    isSelected = false
)