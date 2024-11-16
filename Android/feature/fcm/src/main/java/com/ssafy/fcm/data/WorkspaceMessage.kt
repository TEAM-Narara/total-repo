package com.ssafy.fcm.data

import com.ssafy.fcm.FcmDestination
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkspaceMessage(
    override val type: String,
    override val goTo: FcmDestination,
    val workspaceId: Long,
) : FcmDirection
