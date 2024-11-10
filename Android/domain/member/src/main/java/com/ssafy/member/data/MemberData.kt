package com.ssafy.member.data

import com.ssafy.model.user.User

data class UserData(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val profileImgUrl: String?
)

fun User.toUserData() = UserData(
    memberId = memberId,
    email = email,
    nickname = nickname,
    profileImgUrl = profileImgUrl
)

fun UserData.toUser() = User(
    memberId = memberId,
    email = email,
    nickname = nickname,
    profileImgUrl = profileImgUrl
)
