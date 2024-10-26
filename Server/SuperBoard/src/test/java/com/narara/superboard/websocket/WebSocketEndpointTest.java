package com.narara.superboard.websocket;

import com.narara.superboard.websocket.interfaces.dto.WebSocketBodyDto;
import com.narara.superboard.websocket.interfaces.dto.WebSocketTestDto;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//테스트마다 랜덤포트 사용
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketEndpointTest {
    private final Logger logger = LoggerFactory.getLogger(WebSocketEndpointTest.class);

    private static final String WEBSOCKET_TOPIC = "/topic/test/{boardId}";
    private static final String WEBSOCKET_SEND_URL = "/app/test/update/{boardId}";

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private BlockingQueue<WebSocketTestDto> blockingQueue;

    @BeforeEach
    void setup() throws ExecutionException, InterruptedException, TimeoutException {
        // SockJS 클라이언트 설정
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);

        // JSON 메시지 변환기 설정
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setPrettyPrint(false);
        this.stompClient.setMessageConverter(messageConverter);

        // 메시지 큐 초기화
        this.blockingQueue = new LinkedBlockingQueue<>();

        // WebSocket 연결
        String wsUrl = "http://localhost:" + port + "/ws";
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void handleException(StompSession session, StompCommand command,
                                        StompHeaders headers, byte[] payload, Throwable exception) {
                exception.printStackTrace();
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                logger.info(() -> "Transport 과정에서 에러발생: " + exception.getMessage());
                exception.printStackTrace();
            }

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                logger.info(() -> "WebSocket 연결 완료");
            }
        };

        this.stompSession = stompClient.connect(wsUrl, sessionHandler)
                .get(5, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("WebSocket 연결, 구독 및 메시지 수신 테스트")
    void testWebSocketConnection() throws InterruptedException {
        // Given
        long boardId = 1L;
        WebSocketBodyDto requestDto = new WebSocketBodyDto(1L, "테스트 제목", "테스트 내용");
        String jwt = "test-jwt-token";

        // When: Topic 구독
        StompFrameHandler frameHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return WebSocketTestDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                logger.info(() -> "받은 메시지: " + payload);
                blockingQueue.offer((WebSocketTestDto) payload);
            }
        };

        stompSession.subscribe(
                WEBSOCKET_TOPIC.replace("{boardId}", Long.toString(boardId)),
                frameHandler
        );

        // 메시지 전송
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", jwt);
        headers.setDestination(WEBSOCKET_SEND_URL.replace("{boardId}", Long.toString(boardId)));
        stompSession.send(headers, requestDto);

        // Then: 메시지 수신 확인 - 5초동안 응답대기
        WebSocketTestDto response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertAll(
                () -> assertNotNull(response, "응답이 null이 아니어야 합니다."),
                () -> assertEquals("UPDATE", response.type(), "응답 타입이 UPDATE여야 합니다."),
                () -> assertEquals(requestDto, response.response(), "요청과 응답의 내용이 일치해야 합니다.")
        );
    }

    @AfterEach
    void cleanup() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }
}