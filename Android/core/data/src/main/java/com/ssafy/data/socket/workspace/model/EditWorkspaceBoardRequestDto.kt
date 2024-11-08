package com.ssafy.data.socket.workspace.model

data class EditWorkspaceBoardRequestDto(
    val workspaceId: Long,
    val boardId: Long,
    val boardName: String,
    val backgroundType: String,
    val backgroundValue: String,
    val visibility: String,
    val isClosed: Boolean
)