package com.ssafy.board.updateboard.data

data class BoardData(
    val id: Long,
    val title: String,
    val workspaceTitle: String,
    val background: Any,
    val visibility: String,
)