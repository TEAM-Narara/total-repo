package com.ssafy.data.socket.workspace.model

data class EditArchiveBoardRequestDto(
    val workspaceId: Long,
    val boardId: Long,
    val isArchive: Boolean,
)