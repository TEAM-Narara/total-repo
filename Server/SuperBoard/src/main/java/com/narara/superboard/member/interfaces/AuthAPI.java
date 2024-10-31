package com.narara.superboard.member.interfaces;

import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "AUTH API", description = "인증관련 API")
@RequestMapping("/api/v1/members")
public interface AuthAPI {

    @PostMapping("/login")
    @Operation(summary = "로그인")
    ResponseEntity<?> login(MemberLoginRequestDto memberLoginRequestDto);

    @PostMapping("/register")
    @Operation(summary = "회원가입")
    ResponseEntity<?> register(MemberCreateRequestDto memberCreateRequestDto);

    @GetMapping("/logout")
    @Operation(summary = "로그아웃")
    ResponseEntity<?> logout();

    @GetMapping("/withdrawal")
    @Operation(summary = "회원 탈퇴")
    ResponseEntity<?> withdrawal();

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급")
    ResponseEntity<?> reissueAccessToken(@RequestHeader("Refresh-Token") String refreshToken);

}
