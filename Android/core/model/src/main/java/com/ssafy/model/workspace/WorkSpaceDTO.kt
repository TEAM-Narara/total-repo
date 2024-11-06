package com.ssafy.model.workspace

data class WorkSpaceDTO(
    val workSpaceId: Long,
    val authority: String,
    val name: String,

    @Transient
    val isStatus: String = "STAY"
)