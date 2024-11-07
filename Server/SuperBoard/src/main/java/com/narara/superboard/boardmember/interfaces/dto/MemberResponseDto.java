package com.narara.superboard.boardmember.interfaces.dto;

import lombok.Builder;

@Builder
public record MemberResponseDto(
        Long memberId,
        String memberEmail,
        String memberNickname,
        String memberProfileImgUrl,
        String authority,
        Boolean isDeleted
) {
}
