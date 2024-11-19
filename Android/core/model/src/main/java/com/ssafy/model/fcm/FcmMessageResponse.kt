package com.ssafy.model.fcm

data class FcmMessageResponse(
    val title: String?,
    val time: Long?,
    val type: String,
    val goTo: FcmDestinationResponse,
    val manOfActionId: Long?,
    val manOfActionUrl: String?,
    val workspaceId: Long,
    val boardId: Long,
    val listId: Long,
    val cardId: Long,
)

