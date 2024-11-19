package com.narara.superboard.websocket.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketErrorDto {
    private String type;        // 메시지 타입 (예: "ERROR")
    private String errorMessage; // 구체적인 에러 메시지
    private int errorCode;      // HTTP 상태 코드와 유사한 에러 코드

    // 필요에 따라 추가 필드
    // private String errorDetail;  // 상세 에러 정보
    // private String timestamp;    // 에러 발생 시간
}