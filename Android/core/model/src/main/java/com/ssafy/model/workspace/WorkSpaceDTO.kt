package com.ssafy.model.workspace

import com.ssafy.model.member.Authority
import com.ssafy.model.with.DataStatus

data class WorkSpaceDTO(
    val workSpaceId: Long,
    val authority: Authority,
    val name: String,

    @Transient
    val isStatus: DataStatus? = DataStatus.STAY
)