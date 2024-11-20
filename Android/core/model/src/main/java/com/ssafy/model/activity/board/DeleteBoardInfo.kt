package com.ssafy.model.activity.board

data class DeleteBoardInfo(
    val boardId: Long,
    val boardName: String,
    val workspaceName: String
){
    override fun toString(): String {
        return boardName
    }
}
