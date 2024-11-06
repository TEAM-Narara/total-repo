package com.ssafy.model.with

data class WorkspaceInBoardDTO(
    val id: Long,
    val name: String,
    val authority: String,
    val isStatus: String = "CREATE",
    val boards: List<BoardInListDTO>
)