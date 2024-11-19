package com.narara.superboard.fcmtoken.interfaces;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.fcmtoken.entity.Alarm;
import com.narara.superboard.fcmtoken.entity.FcmToken;
import com.narara.superboard.fcmtoken.interfaces.dto.FcmTokenResponseDto;
import com.narara.superboard.fcmtoken.service.AlarmService;
import com.narara.superboard.fcmtoken.service.FcmTokenService;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "h. FCM 토큰")
@RestController
@RequiredArgsConstructor
public class FcmTokenController implements FcmTokenAPI {

    private final AlarmService alarmService;
    private final FcmTokenService fcmTokenService;

    @GetMapping("myAlarm")
    public ResponseEntity<DefaultResponse<List<Alarm>>> getMyAlarm(@AuthenticationPrincipal Member member) {
        List<Alarm> alarmsByMember = alarmService.getAlarmsByMember(member);

        return new ResponseEntity<>(
                DefaultResponse.res(
                        StatusCode.OK,
                        ResponseMessage.ALARM_FETCH_SUCCESS,
                        alarmsByMember
                ),
                HttpStatus.CREATED
        );
    }

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

    @PostMapping("test")
    public ResponseEntity<Void> testAlarm(@RequestParam String token) throws FirebaseMessagingException {
        String message = FirebaseMessaging.getInstance().send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("asdf")
                        .setBody("asdf")
                        .build())
                .setToken(token)  // 대상 디바이스의 등록 토큰
                .build());

        return ResponseEntity.ok().build();
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

        fcmTokenService.deleteFcmTokenByMember(member);
        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.FCM_TOKEN_DELETE_SUCCESS),
                HttpStatus.OK
        );
    }
}
