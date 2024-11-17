package com.ssafy.model.activity.board

data class UpdateBoardInfo(
    val boardId: Long,
    val boardName: String,
    val workspaceName: String
){
    override fun toString(): String {
        return boardName
    }
}