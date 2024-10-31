package com.narara.superboard.websocket;

import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.member.util.JwtTokenProvider;
import com.narara.superboard.workspace.service.WorkSpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BoardService boardService;

    @Autowired
    private WorkSpaceService workSpaceService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("@#%$#: "+ accessor.getCommand().toString());

        //TODO stomp형식 제대로 파악하는 것도 필요함, 나중에 제대로 정의되면 검증로직을 넣자
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 최초 연결 시 인증
            String token = extractToken(accessor);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                accessor.setUser(auth);
            }
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            // 구독 메시지 수신 시 보드 접근 권한을 검증
            if (accessor.getUser() != null) {
                String destination = accessor.getDestination();
                Long boardId = extractBoardId(destination);
                String username = accessor.getUser().getName();

//                if (!boardService.hasAccess(username, boardId)) {
//                    throw new MessageDeliveryException("No permission for this board");
//                }
            }
        } else if (StompCommand.SEND.equals(accessor.getCommand())) {
            // 변경요청 수신 시, 해당 workspace나 board에 권한이 있는지 검증
            checkMessagePermission(accessor);
        }

        return message;
    }

    private void checkMessagePermission(StompHeaderAccessor accessor) {
        if (accessor.getUser() != null) {
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/app/workspaces/")) {
                Long workspaceId = extractWorkspaceId(destination);
                String username = accessor.getUser().getName();

                log.info("@@@@@@@@@@@@@@@@@@:username: " + username);
//                if (!workSpaceService.hasAccess(username, workspaceId)) {
//                    throw new MessageDeliveryException("No permission for this workspace");
//                }
            }
        }
    }

    private String extractToken(StompHeaderAccessor accessor) {
        List<String> authorization = accessor.getNativeHeader("Authorization");
        if (authorization != null && !authorization.isEmpty()) {
            String token = authorization.get(0);
            if (token.startsWith("Bearer ")) {
                return token.substring(7);
            }
        }
        return null;
    }

    private Long extractBoardId(String destination) {
        String[] parts = destination.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    private Long extractWorkspaceId(String destination) {
        // "/app/workspaces/123/edit" 형식에서 ID 추출
        String[] parts = destination.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("workspaces".equals(parts[i]) && i + 1 < parts.length) {
                return Long.parseLong(parts[i + 1]);
            }
        }
        throw new IllegalArgumentException("Invalid workspace destination format");
    }
}