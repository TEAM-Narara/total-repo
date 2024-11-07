package com.narara.superboard.memberbackground.interfaces.dto;

import com.narara.superboard.memberbackground.entity.MemberBackground;
import lombok.Builder;

@Builder
public record MemberBackgroundResponseDto(
        Long memberBackgroundId,
        String imgUrl
) {
    public static MemberBackgroundResponseDto of(MemberBackground memberBackground){
        return MemberBackgroundResponseDto.builder()
                .memberBackgroundId(memberBackground.getId())
                .imgUrl(memberBackground.getImgUrl())
                .build();
    }
}
