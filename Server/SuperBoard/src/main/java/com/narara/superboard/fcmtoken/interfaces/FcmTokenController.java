package com.narara.superboard.fcmtoken.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.fcmtoken.entity.FcmToken;
import com.narara.superboard.fcmtoken.interfaces.dto.FcmTokenResponseDto;
import com.narara.superboard.fcmtoken.service.FcmTokenService;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FCM 토큰")
@RestController
@RequiredArgsConstructor
public class FcmTokenController implements FcmTokenAPI {

    private final FcmTokenService fcmTokenService;

    @Override
    public ResponseEntity<DefaultResponse<FcmTokenResponseDto>> createFcmToken(
            @RequestParam Long memberId,
            @RequestParam String registrationToken) {

        FcmToken fcmToken = fcmTokenService.createFcmToken(memberId, registrationToken);
        return new ResponseEntity<>(
                (DefaultResponse.res(StatusCode.CREATED, ResponseMessage.FCM_TOKEN_CREATE_SUCCESS,
                        FcmTokenResponseDto.of(fcmToken)))
                , HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<FcmTokenResponseDto>> updateFcmToken(
            @RequestParam Long memberId,
            @RequestParam String registrationToken) {

        FcmToken fcmToken = fcmTokenService.updateFcmToken(memberId, registrationToken);
        return new ResponseEntity<>(
                (DefaultResponse.res(StatusCode.CREATED, ResponseMessage.FCM_TOKEN_CREATE_SUCCESS,
                        FcmTokenResponseDto.of(fcmToken))),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<DefaultResponse<Void>> deleteFcmToken(
            @AuthenticationPrincipal Member member) {

        fcmTokenService.deleteFcmToken(member);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.FCM_TOKEN_DELETE_SUCCESS),
                HttpStatus.OK
        );
    }
}
