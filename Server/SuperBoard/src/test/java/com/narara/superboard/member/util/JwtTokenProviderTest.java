package com.narara.superboard.member.util;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * jwt 기능 단위 테스트 (레드 사이클)
 */
@DisplayName("JwtTokenProvider 대한 Test")
class JwtTokenProviderTest {
    @InjectMocks
    private JwtTokenProviderImpl jwtTokenProvider;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserDetails userDetails;
    @Mock
    private HttpServletRequest request;
    @Mock
    private MemberRepository memberRepository;

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("secret_key".getBytes());
    private static final Key WRONG_SECRET_KEY = Keys.hmacShaKeyFor("wrong_secret_key".getBytes());
    private static final Long userID = 1L;
    private String validToken;
    private String validRefreshToken;
    private String expiredToken;
    private String invalidToken;
    private String expiredRefreshToken;
    private String storedRefreshToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userDetails.getUsername()).thenReturn(userID.toString());
        when(userDetailsService.loadUserByUsername(userID.toString())).thenReturn(userDetails);

        // 유효한 토큰
        validToken = jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(userDetails, null));

        // DB에 저장된 유효한 리프레시 토큰
        storedRefreshToken = jwtTokenProvider.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(userDetails, null));

        // 시간 만료된 토큰
        expiredToken = Jwts.builder()
                .subject(userID.toString())
                .issuedAt(new Date()) // 발행 시간 설정
                .expiration(new Date(System.currentTimeMillis() - 1000)) // 1시간 유효기간 설정
                .signWith(SECRET_KEY) // 보안 키로 서명
                .compact(); // JWT를 직렬화하여 문자열로 변환

        // secret_key 잘못된 토큰
        invalidToken = Jwts.builder()
                .subject(userID.toString())
                .issuedAt(new Date()) // 발행 시간 설정
                .expiration(new Date(System.currentTimeMillis() + 600000)) // 1시간 유효기간 설정
                .signWith(WRONG_SECRET_KEY) // 보안 키로 서명
                .compact(); // JWT를 직렬화하여 문자열로 변환

        // 만료된 expiredRefreshToken
        expiredRefreshToken = Jwts.builder()
                .subject(userID.toString())
                .issuedAt(new Date()) // 발행 시간 설정
                .expiration(new Date(System.currentTimeMillis() - 1000)) // 1시간 유효기간 설정
                .signWith(SECRET_KEY) // 보안 키로 서명
                .compact(); // JWT를 직렬화하여 문자열로 변환

        // Repository Mock 설정
        when(memberRepository.findById(1L).get().getRefreshToken()).thenReturn(storedRefreshToken);
    }

    @DisplayName("token 생성 테스트")
    @Test
    public void testGenerateToken() throws Exception {
        // GIVEN : 사용자 인증 정보 준비
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null);

        // WHEN :  JWT 액세스 토큰 생성
        String token = jwtTokenProvider.generateAccessToken(authenticationToken);

        // THEN
        // Assert: 토큰이 null이 아니어야한다
        assertNotNull(token);

        // JWT 파싱 및 클레임 확인
        Jws<Claims> claimsJws = Jwts.parser().verifyWith((SecretKey) SECRET_KEY).build()
                .parseSignedClaims(token);

        // Assert: 사용자 아이디가 포함되어 있어야 한다.
        assertEquals(userID.toString(), claimsJws.getPayload().getSubject());

        // Assert: 발급 시간(iat)이 포함되어 있어야 한다.
        assertNotNull(claimsJws.getPayload().getIssuedAt());

        // Assert: 유효기간(exp)이 현재 시간 이후여야 한다.
        Date expiration = claimsJws.getPayload().getExpiration();
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @DisplayName("만료된 토큰 검증")
    @Test
    void testValidateToken_expiredToken() {
        // Act & Assert
        assertFalse(jwtTokenProvider.isTokenExpired(expiredToken));  // 만료된 토큰은 false 반환
    }

    @DisplayName("서명이 잘못된 토큰 검증")
    @Test
    void testValidateToken_invalidToken() {
        // Act & Assert
        assertThrows(JwtException.class, () -> {
            jwtTokenProvider.validateToken(invalidToken);  // 잘못된 서명 토큰은 예외 발생
        });
    }

    @DisplayName("빈 토큰 검증")
    @Test
    void testValidateToken_emptyToken() {
        // Act & Assert
        assertFalse(jwtTokenProvider.validateToken(""));  // 빈 토큰은 false 반환
    }

    @DisplayName("만료된 Refresh Token으로 새로운 Access Token 생성 실패")
    @Test
    void testRefreshAccessToken_ExpiredRefreshToken() {
        JwtException exception = assertThrows(JwtException.class, () -> {
            jwtTokenProvider.refreshAccessToken(expiredRefreshToken);
        });
        assertEquals("Invalid Refresh Token", exception.getMessage());
    }

    @DisplayName("만료된 Access Token으로 인증 정보 추출 실패")
    @Test
    void testGetAuthentication_ExpiredToken() {
        assertThrows(JwtException.class, () -> {
            jwtTokenProvider.getAuthentication(expiredToken);
        });
    }

    @DisplayName("만료된 Access Token에서 사용자 ID 추출 실패")
    @Test
    void testGetUserIDFromToken_ExpiredToken() {
        assertThrows(JwtException.class, () -> {
            jwtTokenProvider.getMemberIDFromToken(expiredToken);
        });
    }

    @DisplayName("resolveToken - 유효한 Bearer 토큰 추출")
    @Test
    void testResolveToken_ValidBearerToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        String token = jwtTokenProvider.resolveAccessToken(request);
        assertEquals(validToken, token);
    }

    @DisplayName("resolveToken - 잘못된 형식의 토큰 처리 (Bearer가 없는 우)")
    @Test
    void testResolveToken_InvalidFormat() {
        when(request.getHeader("Authorization")).thenReturn(validToken); // Bearer prefix 없는 경우
        String token = jwtTokenProvider.resolveAccessToken(request);
        assertNull(token);
    }

    @DisplayName("경계 테스트 - 거의 만료된 토큰 처리")
    @Test
    void testToken_NearExpiration() throws InterruptedException {
        // 유효기간이 매우 짧은 토큰 생성
        String nearExpiredToken = Jwts.builder()
                .subject(userID.toString())
                .issuedAt(new Date()) // 발행 시간 설정
                .expiration(new Date(System.currentTimeMillis() + 1))
                .signWith(SECRET_KEY) // 보안 키로 서명
                .compact(); // JWT를 직렬화하여 문자열로 변환

        // 바로 만료 여부 확인 (토큰 생성 직후엔 만료되지 않음)
        assertFalse(jwtTokenProvider.isTokenExpired(nearExpiredToken));

        // 1ms 후 토큰이 만료되도록 대기
        Thread.sleep(2);
        assertTrue(jwtTokenProvider.isTokenExpired(nearExpiredToken));
    }

    @DisplayName("resolveRefreshToken - 유효한 리프레시 토큰이 헤더에서 제대로 추출되는지 테스트")
    @Test
    void testResolveRefreshToken_Valid() {
        when(request.getHeader("X-Refresh-Token")).thenReturn(validToken);
        String extractedToken = jwtTokenProvider.resolveRefreshToken(request);
        assertEquals(validToken, extractedToken);
    }

    @DisplayName("resolveRefreshToken - 헤더가 없는 경우 null이 반환되는지 테스트")
    @Test
    void testResolveRefreshToken_NoHeader() {
        when(request.getHeader("X-Refresh-Token")).thenReturn(null);
        String extractedToken = jwtTokenProvider.resolveRefreshToken(request);
        assertNull(extractedToken);
    }

    @DisplayName("resolveRefreshToken - 헤더에 잘못된 형식의 토큰이 있는 경우 null 또는 빈 값 반환 여부 테스트")
    @Test
    void testResolveRefreshToken_InvalidTokenFormat() {
        when(request.getHeader("X-Refresh-Token")).thenReturn("InvalidToken");
        String extractedToken = jwtTokenProvider.resolveRefreshToken(request);
        assertEquals("InvalidToken", extractedToken);
    }

    @DisplayName("refreshAccessToken - 유효한 리프레시 토큰으로 토큰 갱신 테스트")
    @Test
    void testRefreshAccessToken_WithValidRefreshTokenFromHeader() {
        when(request.getHeader("X-Refresh-Token")).thenReturn(validToken);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        assertNotNull(refreshToken);
        String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
        assertNotNull(newAccessToken);
    }

    @DisplayName("refreshAccessToken - 만료된 리프레시 토큰이 헤더에 있는 경우 토큰 갱신 실패")
    @Test
    void testRefreshAccessToken_WithExpiredRefreshTokenFromHeader() {
        when(request.getHeader("X-Refresh-Token")).thenReturn(expiredRefreshToken);

        JwtException exception = assertThrows(JwtException.class, () -> {
            jwtTokenProvider.refreshAccessToken(expiredRefreshToken);
        });
        assertEquals("Invalid Refresh Token", exception.getMessage());
    }

    @DisplayName("refreshAccessToken - 유효한 리프레시 토큰으로 새로운 액세스 토큰 발급 테스트")
    @Test
    void testRefreshAccessToken_ValidToken() {
        // 정상적으로 리프레시 토큰이 DB에서 조회되고 일치하는 경우
        String newAccessToken = jwtTokenProvider.refreshAccessToken(validRefreshToken);
        assertNotNull(newAccessToken);
    }

    @DisplayName("refreshAccessToken - 리프레시 토큰이 DB에 없을 때 예외 발생 테스트")
    @Test
    void testRefreshAccessToken_TokenNotFound() {
        when(memberRepository.findById(userID).get().getRefreshToken()).thenReturn(null);

        JwtException exception = assertThrows(JwtException.class, () -> {
            jwtTokenProvider.refreshAccessToken(validRefreshToken);
        });
        assertEquals("Invalid Refresh Token", exception.getMessage());
    }

    @DisplayName("refreshAccessToken - DB에 저장된 리프레시 토큰과 일치하지 않을 때 예외 발생 테스트")
    @Test
    void testRefreshAccessToken_TokenMismatch() {
        // DB에서 유저를 조회할 때, 다른 리프레시 토큰이 있는 경우
        Member mockMember = mock(Member.class);
        when(memberRepository.findById(userID)).thenReturn(Optional.of(mockMember));
        when(mockMember.getRefreshToken()).thenReturn("invalidToken");

        // 클라이언트가 제공한 리프레시 토큰과 DB에 저장된 토큰이 일치하지 않음
        JwtException exception = assertThrows(JwtException.class, () -> {
            jwtTokenProvider.refreshAccessToken(validRefreshToken);
        });

        // 예외 메시지가 "Invalid Refresh Token"인지 확인
        assertEquals("Invalid Refresh Token", exception.getMessage());
    }

}