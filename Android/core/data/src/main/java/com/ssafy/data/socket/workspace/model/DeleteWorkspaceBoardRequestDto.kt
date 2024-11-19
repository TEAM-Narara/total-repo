package com.ssafy.data.socket.workspace.model

data class DeleteWorkspaceBoardRequestDto(
    val workspaceId: Long,
    val boardId: Long,
    val isClosed: Boolean,
)