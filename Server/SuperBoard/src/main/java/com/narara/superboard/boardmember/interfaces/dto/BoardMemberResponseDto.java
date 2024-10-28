package com.narara.superboard.boardmember.interfaces.dto;

import lombok.Builder;

@Builder
public record BoardMemberResponseDto(
        Long memberId,
        String memberEmail,
        String memberNickname,
        String memberProfileImgUrl,
        String authority
) {
}
