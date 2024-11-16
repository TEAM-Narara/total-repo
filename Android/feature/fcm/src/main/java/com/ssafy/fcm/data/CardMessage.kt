package com.ssafy.fcm.data

import com.ssafy.fcm.FcmDestination
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardMessage(
    override val type: String,
    override val goTo: FcmDestination,
    override val manOfActionId: Long?,
    val workspaceId: Long,
    val boardId: Long,
    val listId: Long,
    val cardId: Long,
) : FcmDirection
