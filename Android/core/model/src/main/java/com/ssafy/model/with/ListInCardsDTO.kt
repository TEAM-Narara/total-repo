package com.ssafy.model.with

data class ListInCardsDTO(
    val id: Long,
    val boardId: Long,
    val name: String,
    val myOrder: Long,
    val isArchived: Boolean,

    @Transient val isStatus: DataStatus = DataStatus.CREATE,
    @Transient val columnUpdate: Long = 0L,

    val cards: List<CardAllInfoDTO>,
    val listMembers: List<ListMemberDTO>,
    val listMemberAlarm: Boolean = false
)
