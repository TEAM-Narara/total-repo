package com.ssafy.model.with

import com.ssafy.model.member.Authority

data class WorkspaceMemberDTO (
    val id: Long = 0L,
    val memberId: Long = 0L,
    val workspaceId: Long = 0L,
    val authority: Authority,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)
