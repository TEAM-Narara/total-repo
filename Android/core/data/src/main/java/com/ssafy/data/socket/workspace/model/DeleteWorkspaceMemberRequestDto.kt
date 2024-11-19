package com.ssafy.data.socket.workspace.model

data class DeleteWorkspaceMemberRequestDto(
    val workspaceMemberId: Long,
    val workspaceId: Long,
    val memberId: Long,
)