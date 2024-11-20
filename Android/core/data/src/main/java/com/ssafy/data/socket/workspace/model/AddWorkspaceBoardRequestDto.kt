package com.ssafy.data.socket.workspace.model

data class AddWorkspaceBoardRequestDto(
    val workspaceId: Long,
    val boardId: Long,
    val boardName: String,
    val coverType: String,
    val coverValue: String?,
    val visibility: String,
    val isClosed: Boolean
)