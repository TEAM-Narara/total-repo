package com.ssafy.model.user

data class User(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val profileImgUrl: String?,
)
