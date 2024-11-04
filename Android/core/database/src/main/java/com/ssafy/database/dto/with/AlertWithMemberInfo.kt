package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Alert
import com.ssafy.database.dto.Member

data class AlertWithMemberInfo(
    @Embedded val alert: Alert,

    @Relation(
        parentColumn = "memberId",
        entityColumn = "id"
    )
    val member: Member
)