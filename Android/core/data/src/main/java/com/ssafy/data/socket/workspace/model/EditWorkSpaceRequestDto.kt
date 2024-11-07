package com.ssafy.data.socket.workspace.model

data class EditWorkSpaceRequestDto(
    val workspaceId: Long,
    val workspaceName: String,
)