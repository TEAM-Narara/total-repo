package com.ssafy.model.with

data class WorkspaceInBoardDTO(
    val id: Long,
    val name: String,
    val authority: String,
    val isStatus: DataStatus = DataStatus.CREATE,
    val boards: List<BoardInListDTO>
)