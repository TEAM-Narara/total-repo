package com.ssafy.model.member

import com.ssafy.model.user.User

data class SearchMemberResponse(
    val searchMemberResponseDtoList: List<User>,
    val totalPages: Int,
    val totalElements: Long
)
