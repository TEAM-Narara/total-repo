package com.ssafy.model.activity.List

data class ArchiveListInfo(
    val listId: Long,
    val listName: String,
    val isArchived: Boolean,
){
    override fun toString(): String {
        return listName
    }
}
