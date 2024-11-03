package com.ssafy.database.dto.with

data class AlertWithMemberInfo(
    val alertId: Long,
    val memberId: Long,
    val title: String,
    val body: String?,
    val status: String,
    val nickname: String,
    val profileImageUrl: String
)