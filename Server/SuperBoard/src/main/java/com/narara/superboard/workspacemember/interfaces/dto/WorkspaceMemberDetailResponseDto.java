package com.narara.superboard.workspacemember.interfaces.dto;

import lombok.Builder;

@Builder
public record WorkspaceMemberDetailResponseDto(
        Long id,
        String email,
        String name,
        String profileImgUrl,
        String authority
        ) {
}
