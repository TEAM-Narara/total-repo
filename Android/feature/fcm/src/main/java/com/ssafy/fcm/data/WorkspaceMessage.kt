package com.ssafy.fcm.data

import com.ssafy.fcm.FcmDestination
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkspaceMessage(
    override val type: String,
    override val goTo: FcmDestination,
    override val manOfActionId: Long?,
    val workspaceId: Long,
) : FcmDirection
