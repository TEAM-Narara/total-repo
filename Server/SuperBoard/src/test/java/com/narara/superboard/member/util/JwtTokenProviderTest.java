package com.narara.superboard.member.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * jwt 기능 단위 테스트 (레드 사이클)
 */
@SpringBootTest
@Transactional
class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService; // 사용자 세부정보 서비스
    private UserDetails userDetails; // 사용자 세부정보
    private static final String SECRET_KEY = "5ad900a5079ed71ab8bf24752f8cf3025a164ba352a8d85d23ff42973ee4cc53911d5e2d2b4543848265668243df7098d6076380a9bc50ce6d02d8687c3da881";
    private String username = "testUser";
    private String validToken;
    private String expiredToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        userDetailsService = Mockito.mock(UserDetailsService.class);
        jwtTokenProvider = new JwtTokenProvider(userDetailsService);

        userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn(username);
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(userDetails);

        // 유효한 JWT 토큰 생성 (만료 시간 포함)
        validToken = jwtTokenProvider.generateAccessToken(new UsernamePasswordAuthenticationToken(userDetails, null));

        // 만료된 JWT 토큰 생성 (직접 만료 시간을 변경하거나 테스트용으로 만료된 토큰 생성)
        expiredToken = "your_expired_jwt_token"; // 실제 테스트에서는 만료된 토큰을 준비

        // 서명이 잘못된 JWT 토큰 생성
        invalidToken = validToken.substring(0, validToken.length() - 5) + "12345"; // 임의로 서명을 수정
    }

    @DisplayName("accessToken 생성")
    @Test
    public void testGenerateAccessToken() throws Exception {
        // GIVE : 사용자 인증 정보 준비
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null);

        // WHEN :  JWT 액세스 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authenticationToken);

        // THEN
        // Assert: 토큰이 null이 아니고, 특정 형식으로 시작해야 한다.
        assertNotNull(accessToken);
        assertTrue(accessToken.startsWith("eyJ")); // JWT는 항상 "eyJ"로 시작

        // JWT 파싱을 통해 클레임 확인
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        // Assert: 사용자 이름이 포함되어 있어야 한다.
        assertEquals(username, claims.getSubject());

        // Assert: 발급 시간(iat)이 포함되어 있어야 한다.
        assertNotNull(claims.getIssuedAt());

        // Assert: 유효기간(exp)이 현재 시간 이후여야 한다.
        Date expiration = claims.getExpiration();
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken_validToken() {
        // Act & Assert
        assertTrue(jwtTokenProvider.validateToken(validToken));  // 유효한 토큰은 true 반환
    }

    @Test
    void testValidateToken_expiredToken() {
        // Act & Assert
        assertFalse(jwtTokenProvider.isTokenExpired(expiredToken));  // 만료된 토큰은 false 반환
    }

    @Test
    void testValidateToken_invalidToken() {
        // Act & Assert
        assertThrows(JwtException.class, () -> {
            jwtTokenProvider.validateToken(invalidToken);  // 잘못된 서명 토큰은 예외 발생
        });
    }

    @Test
    void testValidateToken_emptyToken() {
        // Act & Assert
        assertFalse(jwtTokenProvider.validateToken(""));  // 빈 토큰은 false 반환
    }


}