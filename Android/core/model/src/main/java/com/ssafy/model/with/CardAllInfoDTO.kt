package com.ssafy.model.with

import com.ssafy.model.background.Cover

data class CardAllInfoDTO(
    val id: Long = 0L,
    val listId: Long = 0L,
    val name: String = "",
    val description: String? = null,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val cover: Cover? = null,
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,

    @Transient val isStatus: DataStatus = DataStatus.STAY,

    val cardLabels: List<CardLabelDTO>,
    val cardMembers: List<CardMemberDTO>,
    val cardMemberAlarm: Boolean = false,
    val cardAttachment: List<AttachmentDTO>,
    val cardReplies: List<ReplyDTO>,
)
