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
import static org.junit.jupiter.api.Assertions.assertNull;

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
        // Heartbeat 간격 설정 (선택적)
        this.stompClient.setDefaultHeartbeat(new long[]{0, 0}); // heartbeat 비활성화
        // 또는 더 긴 간격으로 설정
        // this.stompClient.setDefaultHeartbeat(new long[]{10000, 10000}); // 10초 간격

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

    @Test
    @DisplayName("구독하지 않은 메시지를 수신하지 않는지 테스트")
    void testNoSubscriptionMessage() throws InterruptedException {
        // Given
        long subscribedBoardId = 2L;
        long unsubscribedBoardId = 3L;
        WebSocketBodyDto requestDto = new WebSocketBodyDto(unsubscribedBoardId, "테스트 제목", "테스트 내용");
        String jwt = "test-jwt-token";

        // When: boardId 2에 대해서만 Topic 구독
        StompFrameHandler frameHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return WebSocketTestDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                logger.info(() -> "받은 메시지: " + payload);
                if (payload != null) {  // heartbeat는 null payload를 가질 수 있음
                    blockingQueue.offer((WebSocketTestDto) payload);
                }
            }
        };

        stompSession.subscribe(
                WEBSOCKET_TOPIC.replace("{boardId}", String.valueOf(subscribedBoardId)),
                frameHandler
        );

        // 구독하지 않은 boardId 3로 메시지 전송
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", jwt);
        headers.setDestination(WEBSOCKET_SEND_URL.replace("{boardId}", String.valueOf(unsubscribedBoardId)));
        stompSession.send(headers, requestDto);

        // Then: 지정된 시간 동안 실제 메시지 수신 대기
        WebSocketTestDto response = waitForMessage(5, TimeUnit.SECONDS);
        assertNull(response, "구독하지 않은 토픽의 메시지는 수신되지 않아야 합니다");

        // 구독한 boardId로 메시지 전송 테스트
        headers.setDestination(WEBSOCKET_SEND_URL.replace("{boardId}", String.valueOf(subscribedBoardId)));
        stompSession.send(headers, requestDto);

        WebSocketTestDto subscribedResponse = waitForMessage(5, TimeUnit.SECONDS);

        assertAll(
                () -> assertNotNull(subscribedResponse, "구독한 토픽의 메시지는 정상적으로 수신되어야 합니다"),
                () -> assertEquals("UPDATE", subscribedResponse.type(), "구독한 토픽의 메시지는 UPDATE 타입이어야 합니다"),
                () -> assertEquals(requestDto, subscribedResponse.response(), "구독한 토픽의 메시지 내용이 일치해야 합니다")
        );
    }

    // Heartbeat 메시지를 필터링하고 실제 메시지만 기다리는 헬퍼 메소드
    private WebSocketTestDto waitForMessage(long timeout, TimeUnit unit) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long timeoutInMillis = unit.toMillis(timeout);

        while (System.currentTimeMillis() - startTime < timeoutInMillis) {
            WebSocketTestDto message = blockingQueue.poll(timeoutInMillis - (System.currentTimeMillis() - startTime),
                    TimeUnit.MILLISECONDS);

            if (message != null) {
                return message;
            }
        }
        return null;  // 타임아웃 동안 메시지를 받지 못함
    }

    @AfterEach
    void cleanup() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
            logger.info(() -> "구독 끝났엉");
        }
    }
}