package com.ssafy.model.with

import com.ssafy.model.board.MemberResponseDTO

data class ListInCard(
    val id: Long,
    val name: String,
    val myOrder: Long,
    val isArchived: Boolean,
    val cards: List<CardThumbnail>
)

data class CardThumbnail(
    val id: Long,
    val listId: Long,
    val name: String,
    val description: String?,
    val startAt: Long?,
    val endAt: Long?,
    val coverType: String?,
    val coverValue: String?,
    val myOrder: Long,
    val isArchived: Boolean,
    val replyCount: Int,
    val cardMembers: List<MemberResponseDTO>,
    val cardLabels: List<CardLabelWithLabelDTO>
)
