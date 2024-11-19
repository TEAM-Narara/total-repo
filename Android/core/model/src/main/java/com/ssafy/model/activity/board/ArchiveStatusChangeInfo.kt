package com.ssafy.model.activity.board

data class ArchiveStatusChangeInfo(
    val boardId: Long,
    val boardName: String,
    val isArchived: Boolean
){
    override fun toString(): String {
        return boardName
    }
}
