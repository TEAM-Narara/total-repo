package com.ssafy.model.member

import com.ssafy.model.with.DataStatus

data class SimpleMemberDto(
    val memberId: Long,
    val authority: Authority,

    @Transient
    val isStatus: DataStatus? = DataStatus.STAY
)

data class SimpleCardMemberDto(
    val memberId: Long,
    val isRepresentative: Boolean,

    @Transient
    val isStatus: DataStatus? = DataStatus.STAY
)

data class DetailMemberDto(
    val workspaceMemberId: Long,
    val memberId: Long,
    val authority: Authority,

    @Transient
    val isStatus: DataStatus? = DataStatus.STAY
)

enum class Authority(name: String) {
    ADMIN("ADMIN"),
    MEMBER("MEMBER")
}