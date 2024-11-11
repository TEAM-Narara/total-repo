package com.ssafy.model.with

import com.ssafy.model.label.LabelDTO

data class BoardInListDTO(
    val id: Long,
    val workspaceId: Long = 0L,
    val name: String,
    val coverType: String?,
    val coverValue: String?,
    val visibility: String,
    val isClosed: Boolean,

    @Transient val isStatus: DataStatus = DataStatus.STAY,

    val lists: List<ListInCardsDTO>,
    val labels: List<LabelDTO> = emptyList(),
    val boardMembers: List<BoardMemberDTO> = emptyList(),
    val isBoardMyWatch: Boolean
)
