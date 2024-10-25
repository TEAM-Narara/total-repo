package com.narara.superboard.websocket;

import com.narara.superboard.websocket.interfaces.dto.WebSocketBodyDto;
import com.narara.superboard.websocket.interfaces.dto.WebSocketTestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Controller
public class WebSocketTestController {
    @MessageMapping("/test/update")
    @SendTo("/topic/test")
    public WebSocketTestDto testWebsocket(WebSocketBodyDto request) throws InterruptedException {
        log.info("/topic/test 입장, 비즈니스 로직 수행");
        Thread.sleep(1000);

        return new WebSocketTestDto("UPDATE", request);
    }
}
