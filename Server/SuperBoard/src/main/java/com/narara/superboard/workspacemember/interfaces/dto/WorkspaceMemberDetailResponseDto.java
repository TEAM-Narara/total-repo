package com.narara.superboard.workspacemember.interfaces.dto;

public record WorkspaceMemberDetailResponseDto(
        Long memberId,
        String email,
        String name,
        String profileImgUrl,
        String authority
        ) {
}
