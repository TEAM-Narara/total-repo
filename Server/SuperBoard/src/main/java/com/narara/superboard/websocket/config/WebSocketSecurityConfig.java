//package com.narara.superboard.websocket.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
//import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
//import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//
//@Configuration
//@EnableWebSocketMessageBroker
//@EnableWebSocketSecurity
//public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
//    @Override
//    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//        messages
//                .simpDestMatchers("/app/**").authenticated()  // 애플리케이션 메시지 엔드포인트 보안
//                .simpSubscribeDestMatchers("/topic/board/**").authenticated()  // 구독 엔드포인트 보안
//                .anyMessage().authenticated();  // 기타 모든 메시지는 인증 필요
//    }
//
//    @Override
//    protected boolean sameOriginDisabled() {
//        return true;  // CSRF 보호 비활성화 (필요한 경우)
//    }
//}
//