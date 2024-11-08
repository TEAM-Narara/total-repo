package com.ssafy.model.member

import com.ssafy.model.with.DataStatus

data class SimpleMemberDto(
    val memberId: Long,
    val authority: String,

    @Transient
    val isStatus: DataStatus? = DataStatus.STAY
)

data class DetailMemberDto(
    val workspaceMemberId: Long,
    val memberId: Long,
    val authority: String,

    @Transient
    val isStatus: DataStatus? = DataStatus.STAY
)
