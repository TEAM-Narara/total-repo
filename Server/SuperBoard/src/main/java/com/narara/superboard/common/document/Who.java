package com.narara.superboard.common.document;

import com.narara.superboard.member.entity.Member;
import lombok.Builder;

@Builder
public record Who(
        Long memberId,
        String memberNickname
) {
    public static Who of(Member member) {
        return Who.builder()
                .memberId(member.getId())
                .memberNickname(member.getNickname())
                .build();
    }
}
