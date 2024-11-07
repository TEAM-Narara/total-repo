package com.ssafy.model.with

data class WorkspaceMemberDTO (
    val id: Long = 0L,
    val memberId: Long = 0L,
    val workspaceId: Long = 0L,
    val authority: String = "",

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)
