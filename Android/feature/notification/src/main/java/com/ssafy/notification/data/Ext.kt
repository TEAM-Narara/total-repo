package com.ssafy.notification.data

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun NotificationData.toContentString() = buildAnnotatedString {
    // TODO : Notification data에 따라 다르게 나오도록 수정
    val userName = "홍길동"
    val boardName = "새로운 보드"
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(userName)
    }
    append(KoreanPostposition.iGa(userName))
    append(" ")
    append("당신을 ")
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(boardName)
    }
    append(" board")
    append("에 ")
    append("초대하였습니다.")
}