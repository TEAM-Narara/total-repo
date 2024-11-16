package com.ssafy.data.socket.workspace.model

data class AddWorkspaceMemberDto(
    val workspaceMemberId: Long,
    val workspaceId: Long,
    val memberId: Long,
    val memberName: String,
    val authority: String,
    val memberEmail: String,
    val profileImgUrl: String?,
    val isDeleted: Boolean
)