package com.narara.superboard.member.interfaces.dto;

public record MemberLoginResponseDto(MemberDto memberDto,
                                     TokenDto tokenDto) {
}
