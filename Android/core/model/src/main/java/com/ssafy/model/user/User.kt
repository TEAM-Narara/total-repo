package com.ssafy.model.user

data class User(
    val id: Long = 0L,
    val email: String,
    val nickname: String,
    val profileImage: String?,
)
