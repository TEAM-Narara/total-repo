package com.ssafy.model.with

data class CardAllInfoDTO(
    val id: Long = 0L,
    val listId: Long = 0L,
    val name: String = "",
    val description: String? = null,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val coverType: String? = null,
    val coverValue: String? = null,
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,

    @Transient val isStatus: DataStatus = DataStatus.STAY,
    @Transient val columnUpdate: Long = 0L,

    val cardLabels: List<CardLabelDTO>,
    val cardMembers: List<CardMemberDTO>,
    val cardMemberAlarm: Boolean = false,
    val cardAttachment: List<AttachmentDTO>,
    val cardReplies: List<ReplyDTO>,
)
