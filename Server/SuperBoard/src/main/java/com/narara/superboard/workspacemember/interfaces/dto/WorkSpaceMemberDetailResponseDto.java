package com.narara.superboard.workspacemember.interfaces.dto;

import lombok.Builder;

@Builder
public record WorkSpaceMemberDetailResponseDto(
        Long memberId,
        String memberEmail,
        String memberNickname,
        String memberProfileImgUrl,
        String authority
        ) {
}
