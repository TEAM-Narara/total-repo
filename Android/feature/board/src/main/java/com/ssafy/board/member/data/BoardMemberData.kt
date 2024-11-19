package com.ssafy.board.member.data

data class BoardMemberData(
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