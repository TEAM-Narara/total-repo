package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.member.Authority
import com.ssafy.model.with.DataStatus

@Entity(tableName = "workspace")
data class WorkspaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String = "",
    val authority: Authority = Authority.MEMBER,
//    val offset: Long = 0L,

    val isStatus: DataStatus = DataStatus.STAY
)