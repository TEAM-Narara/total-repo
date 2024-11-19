package com.ssafy.model.list

data class ListMoveUpdateListRequestDTO(
    val moveRequest: List<ListMoveUpdateRequestDTO>
)


data class ListMoveUpdateRequestDTO(
    val listId: Long,
    val myOrder: Long
)
