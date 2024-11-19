package com.ssafy.notification.fcm.data

import com.ssafy.notification.fcm.FcmDestination
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeMessage(
    override val title: String? = null,
    override val time: Long? = null,
    override val type: String,
    override val goTo: FcmDestination,
    override val manOfActionId: Long?,
    override val manOfActionUrl: String? = null,
) : FcmMessage
