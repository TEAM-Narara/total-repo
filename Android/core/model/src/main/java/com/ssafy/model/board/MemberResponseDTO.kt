package com.ssafy.model.board

import com.ssafy.model.with.DataStatus

data class MemberResponseDTO(
    val memberId: Long,
    val authority: String,
    val is_representative: Boolean,
    val memberEmail: String,
    val memberNickname: String,
    val memberProfileImgUrl: String,

    @Transient
    val componentId: Long = 0L,
    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)
