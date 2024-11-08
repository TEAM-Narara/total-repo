package com.ssafy.data.socket.workspace.model

data class EditWorkspaceMemberRequestDto(
    val workspaceId: Long,
    val memberId: Long,
    val authority: String,
)