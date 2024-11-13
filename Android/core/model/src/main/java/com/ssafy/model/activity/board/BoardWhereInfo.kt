package com.ssafy.model.activity.board

data class BoardWhereInfo(
    val boardId: Long,
    val boardName: String
){
    override fun toString(): String {
        return boardName
    }
}
