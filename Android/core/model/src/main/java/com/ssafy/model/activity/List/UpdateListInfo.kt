package com.ssafy.model.activity.List

data class UpdateListInfo(
    val listId: Long,
    val listName: String,
){
    override fun toString(): String {
        return listName
    }
}
