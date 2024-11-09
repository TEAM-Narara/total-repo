package com.ssafy.database.dto.with

import com.ssafy.model.with.DataStatus

data class MemberWithRepresentative(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val isRepresentative: Boolean,
)