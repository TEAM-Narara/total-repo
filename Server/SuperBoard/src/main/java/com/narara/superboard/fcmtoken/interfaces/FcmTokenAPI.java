package com.narara.superboard.fcmtoken.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.fcmtoken.interfaces.dto.FcmTokenResponseDto;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/fcm-token")
public interface FcmTokenAPI {

    @PostMapping
    @Operation(summary = "FCM 토큰 생성", description = "회원의 FCM 토큰을 생성합니다.")
    ResponseEntity<DefaultResponse<FcmTokenResponseDto>> createFcmToken(
            @RequestParam Long memberId,
            @RequestParam String registrationToken);

    @PatchMapping
    @Operation(summary = "FCM 토큰 수정", description = "회원의 FCM 토큰을 업데이트합니다.")
    ResponseEntity<DefaultResponse<FcmTokenResponseDto>> updateFcmToken(
            @RequestParam Long memberId,
            @RequestParam String registrationToken);

    @DeleteMapping("/{memberId}")
    @Operation(summary = "FCM 토큰 삭제", description = "회원의 FCM 토큰을 삭제합니다.")
    ResponseEntity<DefaultResponse<Void>> deleteFcmToken(
            @AuthenticationPrincipal Member member);
}
