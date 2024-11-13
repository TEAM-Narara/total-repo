package com.ssafy.model.activity.List

data class CreateListInfo(
    val listId: Long,
    val listName: String,
    val boardId: Long,
){
    override fun toString(): String {
        return listName
    }
}
