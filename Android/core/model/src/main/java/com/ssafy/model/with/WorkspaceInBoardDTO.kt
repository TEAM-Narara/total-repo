package com.ssafy.model.with

import com.ssafy.model.member.Authority

data class WorkspaceInBoardDTO(
    val id: Long,
    val name: String,
    val authority: Authority,
    val isStatus: DataStatus = DataStatus.CREATE,
    val boards: List<BoardInListDTO>
)