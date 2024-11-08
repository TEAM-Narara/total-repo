package com.ssafy.home.data

import androidx.compose.runtime.Immutable
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.member.Authority

@Immutable
data class DetailWorkspaceData(
    val workspaceId: Long,
    val workspaceName: String,
    val members: List<MemberData>
)

@Immutable
data class MemberData(
    val memberId: Long,
    val authority: Authority,
    val memberEmail: String,
    val memberNickname: String,
    val memberProfileImgUrl: String?
)

fun MemberResponseDTO.toMemberData(): MemberData {
    return MemberData(
        memberId = memberId,
        authority = authority,
        memberEmail = memberEmail,
        memberNickname = memberNickname,
        memberProfileImgUrl = memberProfileImgUrl
    )
}
