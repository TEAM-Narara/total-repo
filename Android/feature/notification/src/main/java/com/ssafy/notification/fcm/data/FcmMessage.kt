package com.ssafy.notification.fcm.data

import android.os.Parcelable
import com.ssafy.notification.fcm.FcmDestination

sealed interface FcmMessage : Parcelable {
    val title: String?
    val time: Long?
    val type: String
    val goTo: FcmDestination
    val manOfActionId: Long?
    val manOfActionUrl: String?
}
