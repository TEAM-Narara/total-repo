package com.ssafy.data.socket.workspace.model

import com.ssafy.model.member.Authority

data class EditWorkspaceMemberRequestDto(
    val workspaceMemberId: Long,
    val workspaceId: Long,
    val memberId: Long,
    val authority: Authority,
)