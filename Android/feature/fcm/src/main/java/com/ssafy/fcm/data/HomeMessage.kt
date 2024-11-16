package com.ssafy.fcm.data

import com.ssafy.fcm.FcmDestination
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeMessage(
    override val type: String,
    override val goTo: FcmDestination,
) : FcmDirection
