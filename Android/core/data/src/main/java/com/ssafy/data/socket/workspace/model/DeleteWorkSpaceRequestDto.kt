package com.ssafy.data.socket.workspace.model

data class DeleteWorkSpaceRequestDto(
    val workspaceId: Long,
    val isDeleted: Boolean,
)