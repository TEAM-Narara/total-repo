package com.ssafy.data.socket.workspace.model

data class AddWorkspaceMemberDto(
    val workspaceId: Long,
    val memberId: Long,
    val memberName: String,
    val authority: String,
)