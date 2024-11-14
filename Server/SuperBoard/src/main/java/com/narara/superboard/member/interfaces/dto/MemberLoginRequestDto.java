package com.narara.superboard.member.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberLoginRequestDto(
        @Schema(description = "사용자 이메일", example = "ahaha@naver.com", defaultValue = "ahaha@naver.com") String email,
        @Schema(description = "사용자 비밀번호", example = "1234", defaultValue = "1234") String password) {
}