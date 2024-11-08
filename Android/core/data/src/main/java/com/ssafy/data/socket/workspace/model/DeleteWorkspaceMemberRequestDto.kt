package com.ssafy.data.socket.workspace.model

data class DeleteWorkspaceMemberRequestDto(
    val workspaceId: Long,
    val memberId: Long,
)