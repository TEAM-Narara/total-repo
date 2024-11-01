package com.narara.superboard.member.interfaces.dto;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.enums.LoginType;
import com.narara.superboard.member.exception.MemberNotFoundException;

public record MemberResponseDto(Long memberId, String email, String nickname,
                                String profileImgUrl,String refreshToken,
                                Boolean isDeleted,LoginType loginType) {

    // Member 객체를 받아서 MemberResponseDto로 변환하는 팩토리 메서드
    public static MemberResponseDto from(Member member) {
        if (member == null) {
            throw new MemberNotFoundException(member.getId());
        }

        return new MemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImgUrl(),
                member.getRefreshToken(),
                member.getIsDeleted(),
                member.getLoginType()
        );
    }
}
