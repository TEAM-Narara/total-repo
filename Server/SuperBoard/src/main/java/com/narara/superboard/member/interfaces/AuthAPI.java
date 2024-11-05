package com.narara.superboard.member.interfaces;

import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "인증관련 API")
@RequestMapping("/api/v1/members")
public interface AuthAPI {

    @PostMapping("/login")
    @Operation(summary = "로그인")
    ResponseEntity<?> login(MemberLoginRequestDto memberLoginRequestDto);

    @PostMapping("/register")
    @Operation(summary = "회원가입")
    ResponseEntity<?> register(MemberCreateRequestDto memberCreateRequestDto);

    @PostMapping("/oauth2/login/{provider}")
    @Operation(summary = "소셜 로그인")
    ResponseEntity<?> oauth2Login(@RequestParam("accessToken") String accessToken
            ,@PathVariable("provider") String provider);

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
