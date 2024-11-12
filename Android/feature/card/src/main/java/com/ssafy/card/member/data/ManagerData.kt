package com.ssafy.card.member.data

data class ManagerData(
    val id: Long,
    val nickname: String,
    val email: String,
    val profileUrl: String?,
    val isManager: Boolean,
)