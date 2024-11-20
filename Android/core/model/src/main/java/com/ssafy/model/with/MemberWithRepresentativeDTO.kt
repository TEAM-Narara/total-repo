package com.ssafy.model.with

data class MemberWithRepresentativeDTO(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val isRepresentative: Boolean,
)