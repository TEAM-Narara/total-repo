package com.ssafy.data.socket.workspace.model

import com.ssafy.model.member.Authority

data class AddWorkspaceMemberDto(
    val workspaceId: Long,
    val memberId: Long,
    val memberName: String,
    val authority: Authority,
)