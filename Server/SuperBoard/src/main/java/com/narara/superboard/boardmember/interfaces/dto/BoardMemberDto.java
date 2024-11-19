package com.narara.superboard.boardmember.interfaces.dto;

import lombok.Builder;

@Builder
public record BoardMemberDto(
        Long boardId,
        Long boardMemberId,
        Long memberId,
        String memberEmail,
        String memberNickname,
        String memberProfileImgUrl,
        String authority,
        Boolean isDeleted
) {
}
