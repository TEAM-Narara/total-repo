package com.narara.superboard.member.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    @Mock
    private MemberRepository memberRepository; // 사용자 저장소 Mock
    @Mock
    private JwtTokenProvider jwtTokenProvider; // JWT Token Provider Mock
    @Mock
    private PasswordEncoder passwordEncoder; // 비밀번호 암호화 Mock
    @Mock
    private HttpServletResponse response; // HTTP 응답 Mock

    @InjectMocks
    private AuthServiceImpl authService; // 테스트 대상 서비스

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void registerSuccess() {
        // 1. 회원가입 요청 DTO 생성
        MemberCreateRequestDto requestDto = new MemberCreateRequestDto("test@test.com", "test22", "password123");
        // 2. 토큰 생성 Mocking
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        // 3. 비밀번호 암호화 Mocking
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // 4. 사용자 저장 로직 Mocking
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setRefreshToken("refresh-token"); // 토큰 저장 시점
            return member;
        });

        // 5. 회원가입 호출
        authService.register(requestDto);

        // 6. 결과 검증
        verify(memberRepository, times(1)).save(any(Member.class)); // 사용자 저장 호출 확인
        verify(response).setHeader("Authorization", "Bearer access-token"); // 응답 헤더에 Access Token 포함 여부 확인
        verify(response).setHeader("X-Refresh-Token", "refresh-token"); // 응답 헤더에 Refresh Token 포함 여부 확인
    }
}