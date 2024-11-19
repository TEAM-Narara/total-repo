package com.narara.superboard.websocket.exception;

import com.narara.superboard.websocket.interfaces.dto.WebSocketErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class WebSocketExceptionHandler {
    // 전역 메시지 예외 핸들러
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public WebSocketErrorDto handleException(Exception e) {
        log.error("Global message error handler: ", e);
        return new WebSocketErrorDto(
                "ERROR",
                "서버에서 에러가 발생했습니다: " + e.getMessage(),
                500
        );
    }
}
