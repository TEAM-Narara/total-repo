package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.workspace.WorkSpaceDTO

@Entity(tableName = "workspace")
data class WorkspaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String = "",
    val authority: String,
//    val offset: Long = 0L,

    val isStatus: String = "STAY"
)