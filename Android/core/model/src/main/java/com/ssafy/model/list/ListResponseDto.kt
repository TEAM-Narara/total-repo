package com.ssafy.model.list

data class ListResponseDto(
    val boardId: Long,
    val listId: Long,
    val myOrder: Long,
    val isArchived: Boolean,
    val name: String
)
