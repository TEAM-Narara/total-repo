package com.ssafy.home.member.data

data class WorkspaceMemberData(
    val id: Long,
    val nickname: String,
    val email: String,
    val auth: String,
)

data class SearchMemberData(
    val id: Long,
    val nickname: String,
    val email: String,
    val isInvited: Boolean,
)