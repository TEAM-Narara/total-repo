package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.Alert
import com.ssafy.database.dto.Member

data class AlertWithMemberInfo(
    @Embedded(prefix = "alert_") val alert: Alert,

    @Embedded(prefix = "member_") val member: Member
)