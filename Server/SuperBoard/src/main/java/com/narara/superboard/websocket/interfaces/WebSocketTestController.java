package com.narara.superboard.websocket.interfaces;

import com.narara.superboard.websocket.interfaces.dto.WebSocketBodyDto;
import com.narara.superboard.websocket.interfaces.dto.WebSocketTestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Controller
public class WebSocketTestController {
    private final SimpMessagingTemplate messagingTemplate;

    /*
     * <request> /app/test/update
     * {
     *   "cardId" 1,
     *   "title" "바꿀 타이틀",
     *   "description": "바꿀 description"
     * }
     *
     * <response> /topic/test 를 구독하는 모든 사용자들에게
     * {
     *   "type": "UPDATE",
     *   "response": {
     *     "id": 1,
     *     "title": "바뀐 타이틀",
     *     "description" "바뀐 내용"
     *   }
     * }
     */
    @MessageMapping("/test/update/{boardId}")
    @SendTo("/topic/test/{boardId}")
    public WebSocketTestDto testWebsocket(@DestinationVariable Long boardId, WebSocketBodyDto request,
                                          SimpMessageHeaderAccessor headerAccessor) throws InterruptedException {
        log.info("boardId: " + boardId);
        Long a = 123L;

        // 헤더에서 Authorization 토큰 가져오기 @TODO 로그인 연결 시 삭제
        String authorization = headerAccessor.getFirstNativeHeader("Authorization");
        log.info("Authorization Token: {}", authorization);

        log.info("/topic/test 입장, 비즈니스 로직 수행");
        Thread.sleep(1000);

//        throw new RuntimeException("asdf");
        return new WebSocketTestDto("UPDATE", request);
    }
}
