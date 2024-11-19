package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.AlertEntity
import com.ssafy.database.dto.MemberEntity

data class AlertWithMemberInfo(
    @Embedded(prefix = "alert_") val alert: AlertEntity,

    @Embedded(prefix = "member_") val member: MemberEntity
)