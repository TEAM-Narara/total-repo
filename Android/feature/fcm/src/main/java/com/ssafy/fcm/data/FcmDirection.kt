package com.ssafy.fcm.data

import android.os.Parcelable
import com.ssafy.fcm.FcmDestination

sealed interface FcmDirection : Parcelable {
    val type: String
    val goTo: FcmDestination
}
