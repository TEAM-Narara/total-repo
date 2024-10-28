package com.narara.superboard.member.service;

import com.narara.superboard.common.exception.redis.RedisDataSaveException;
import com.narara.superboard.common.infrastructure.redis.RedisService;
import com.narara.superboard.common.infrastructure.redis.RedisServiceImpl;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.enums.LoginType;
import com.narara.superboard.member.exception.*;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import com.narara.superboard.member.service.validator.MemberValidator;
import com.narara.superboard.member.util.JwtTokenProvider;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNull;

class AuthServiceImplTest {
    @Mock
    private MemberRepository memberRepository; // 사용자 저장소 Mock
    @Mock
    private JwtTokenProvider jwtTokenProvider; // JWT Token Provider Mock
    @Mock
    private PasswordEncoder passwordEncoder; // 비밀번호 암호화 Mock

    @Mock
    private MemberValidator memberValidator;

    @InjectMocks
    private AuthServiceImpl authService; // 테스트 대상 서비스

    @Mock
    private RedisServiceImpl redisService;

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 회원가입 TEST ----------------------------------------------------------------------------------
     */

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerSuccess() {
        // 1. 회원가입 요청 DTO 생성
        MemberCreateRequestDto requestDto = new MemberCreateRequestDto("test@test.com", "test22", "1234");

        // 2. 토큰 생성 Mocking
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        // 3. 비밀번호 암호화 Mocking
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // 4. 사용자 저장 로직 Mocking
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setId(1L);  // id 값 수동 설정 //테스트에서는 save해도 id값 반환 안됨
            member.setRefreshToken("refresh-token"); // 토큰 저장 시점
            return member;
        });

        // 5. 회원가입 호출
        authService.register(requestDto);

        // 6. MemberValidator가 검증을 호출했는지 확인
        verify(memberValidator, times(1)).registerValidate(requestDto); // 유효성 검증 호출 확인

        // 6. 결과 검증
        // 호출되었는지 확인
        verify(memberRepository, times(2)).save(any(Member.class)); // 사용자 저장 호출 확인
        verify(passwordEncoder, times(1)).encode(requestDto.password()); // 비밀번호 암호화 확인
        verify(jwtTokenProvider, times(1)).generateAccessToken(any()); // Access Token 생성 호출 확인
        verify(jwtTokenProvider, times(1)).generateRefreshToken(any()); // Refresh Token 생성 호출 확인
    }

    // 1. 이메일이 없을 때 테스트
    @Test
    @DisplayName("회원가입 실패 테스트 - 이메일 없음")
    void registerFailEmailEmpty() {
        // Given: 이메일이 없는 회원가입 요청 DTO
        MemberCreateRequestDto requestDto = new MemberCreateRequestDto(null, "nickname", "password123");

        // Mocking: 이메일 검증에서 예외를 던지도록 설정
        doThrow(new MemberEmailNotFoundException()).when(memberValidator).registerValidate(requestDto);

        // When & Then: 이메일 검증에서 예외가 발생하는지 확인
        assertThrows(MemberEmailNotFoundException.class, () -> authService.register(requestDto));

        // MemberValidator의 이메일 유효성 검증이 호출되었는지 확인 (검증을 수행했다는 의미)
        verify(memberValidator, times(1)).registerValidate(requestDto);
    }

    // 2. 이메일 형식이 잘못되었을 때 테스트
    @Test
    @DisplayName("회원가입 실패 테스트 - 이메일 형식 잘못됨")
    void registerFailEmailInvalidFormat() {
        // Given: 이메일 형식이 잘못된 회원가입 요청 DTO
        MemberCreateRequestDto requestDto = new MemberCreateRequestDto("invalid-email", "nickname", "password123");

        // Mocking: 이메일 검증에서 예외를 던지도록 설정
        doThrow(new MemberInvalidEmailFormatException()).when(memberValidator).registerValidate(requestDto);

        // When & Then: 잘못된 이메일 형식일 때 예외 발생
        assertThrows(MemberInvalidEmailFormatException.class, () -> authService.register(requestDto));
    }

    // 3. 닉네임이 없을 때 테스트
    @Test
    @DisplayName("회원가입 실패 테스트 - 닉네임 없음")
    void registerFailNicknameEmpty() {
        // Given: 닉네임이 없는 회원가입 요청 DTO
        MemberCreateRequestDto requestDto = new MemberCreateRequestDto("test@test.com", "", "password123");

        // Mocking: 이메일 검증에서 예외를 던지도록 설정
        doThrow(new MemberNicknameNotFoundException()).when(memberValidator).registerValidate(requestDto);

        // When & Then: 닉네임이 없을 때 예외 발생
        assertThrows(MemberNicknameNotFoundException.class, () -> authService.register(requestDto));
    }

    // 4. 비밀번호가 없을 때 테스트
    @Test
    @DisplayName("회원가입 실패 테스트 - 비밀번호 없음")
    void registerFailPasswordEmpty() {
        // Given: 비밀번호가 없는 회원가입 요청 DTO
        MemberCreateRequestDto requestDto = new MemberCreateRequestDto("test@test.com", "nickname", "");

        doThrow(new MemberPasswordNotFoundException()).when(memberValidator).registerValidate(requestDto);

        // When & Then: 비밀번호가 없을 때 예외 발생
        assertThrows(MemberPasswordNotFoundException.class, () -> authService.register(requestDto));
    }

    // 5. 비밀번호가 너무 길때 테스트
    @Test
    @DisplayName("회원가입 실패 테스트 - 비밀번호 너무 길때")
    void registerFailPasswordTooLong() {
        // Given: 너무 긴 비밀번호 (31자리)
        MemberCreateRequestDto requestDto = new MemberCreateRequestDto("test@test.com", "nickname", "a".repeat(31));

        doThrow(new MemberInvalidPasswordFormatException()).when(memberValidator).registerValidate(requestDto);

        // When & Then: 비밀번호가 너무 길 때 예외 발생
        assertThrows(MemberInvalidPasswordFormatException.class, () -> authService.register(requestDto));
    }


    /**
     * 로그인 TEST ----------------------------------------------------------------------------------
     */
    // 1. 로그인 성공 테스트
    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        // Given: 존재하는 사용자의 이메일과 올바른 비밀번호
        String email = "test@naver.com";
        String password = "1111";
        MemberLoginRequestDto requestDto = new MemberLoginRequestDto(email, password);

        // 사용자를 찾았고, 비밀번호도 일치하는 경우
        Member existingMember = new Member(1L, "testNickname", email,"1111","");
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.matches(password, existingMember.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        // When: 로그인 시도
        TokenDto token = authService.login(requestDto);

        // Then: 로그인 성공 (JWT 토큰 생성 확인)
        assertNotNull(token.accessToken());
        assertNotNull(token.refreshToken());
        verify(memberRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, existingMember.getPassword());
        verify(jwtTokenProvider, times(1)).generateAccessToken(any());
        verify(jwtTokenProvider, times(1)).generateRefreshToken(any());
    }

    // 2. 로그인 실패 테스트 - 이메일이 존재하지 않는 경우
    @Test
    @DisplayName("로그인 실패 테스트 - 이메일 존재하지 않음")
    void loginFailEmailNotFound() {
        // Given: 존재하지 않는 이메일
        String email = "nonexistent@test.com";
        String password = "password123";
        MemberLoginRequestDto requestDto = new MemberLoginRequestDto(email, password);

        // 사용자를 찾을 수 없는 경우
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then: 예외 발생
        // login을 실행했을 때, 예외 발생하는지 확인
        assertThrows(InvalidCredentialsException.class, () -> authService.login(requestDto));

        // verify that passwordEncoder and jwtTokenProvider are never called
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    // 3. 로그인 실패 테스트 - 비밀번호 불일치
    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void loginFailIncorrectPassword() {
        // Given: 존재하는 이메일이지만 비밀번호가 틀린 경우
        String email = "test@naver.com";
        String password = "password";
        MemberLoginRequestDto requestDto = new MemberLoginRequestDto(email, password);

        Member existingMember = new Member(1L, "testNickname", email,"wrongPassword","");
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.matches(password, existingMember.getPassword())).thenReturn(false);  // 비밀번호 불일치

        // When & Then: 비밀번호가 틀리면 예외 발생
        assertThrows(InvalidCredentialsException.class, () -> authService.login(requestDto));

        verify(memberRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, existingMember.getPassword());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 계정 탈퇴된 경우")
    void loginFailAccountDeleted() {
        // Given: 존재하는 이메일이지만 계정이 탈퇴된 경우
        String email = "test@naver.com";
        String password = "password123";
        MemberLoginRequestDto requestDto = new MemberLoginRequestDto(email, password);

        // 사용자를 찾았지만 탈퇴된 계정
        Member deletedMember = new Member(1L, "testNickname", email, "encodedPassword","");
        deletedMember.setIsDeleted(true);  // 계정 탈퇴 상태 설정

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(deletedMember));

        // When & Then: 계정이 탈퇴된 경우 예외 발생
        assertThrows(AccountDeletedException.class, () -> authService.login(requestDto));

        // 비밀번호 검증이나 토큰 생성이 호출되지 않았는지 확인
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    /**
     * 로그아웃 TEST ----------------------------------------------------------------------------------
     */
    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logoutSuccess() {
        // Given: 존재하는 회원
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setRefreshToken("refresh-token"); // 기존 토큰 설정

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When: 로그아웃 호출
        authService.logout(memberId);

        // Then: 회원의 refreshToken이 null로 설정되었는지 확인
        assertNull("로그아웃 시 refreshToken은 null이어야 합니다.",member.getRefreshToken());
        verify(memberRepository, times(1)).save(member); // 회원 정보 저장 호출 확인
    }

    @Test
    @DisplayName("로그아웃 실패 테스트 - 존재하지 않는 회원")
    void logoutFail_NonExistentMember() {
        // Given: 존재하지 않는 회원 ID
        Long memberId = 99L;

        // When & Then: 회원을 찾을 수 없는 경우 예외 발생
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> authService.logout(memberId));
    }

    /**
     * 회원 탈퇴 TEST ----------------------------------------------------------------------------------
     */
    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void withdrawalSuccess() {
        // Given: 존재하는 회원 ID
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setIsDeleted(false);
        member.setRefreshToken("refresh-token"); // 기존 토큰 설정

        // Mocking: 회원 조회 시 성공적으로 회원 객체 반환
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When: 회원 탈퇴 호출
        authService.withdrawal(memberId);

        // Then: 회원 탈퇴 상태로 변경되었는지 확인
        assertTrue(member.getIsDeleted()); // 회원이 탈퇴 처리되었는지 확인
        // Then: 회원의 refreshToken이 null로 설정되었는지 확인
        assertNull("로그아웃 시 refreshToken은 null이어야 합니다.",member.getRefreshToken());
        verify(memberRepository, times(1)).save(member); // save 메서드가 호출되었는지 확인
    }

    @Test
    @DisplayName("회원 탈퇴 실패 테스트 - 존재하지 않는 회원")
    void withdrawalFail_NonExistentMember() {
        // Given: 존재하지 않는 회원 ID
        Long memberId = 99L;

        // When & Then: 회원을 찾을 수 없는 경우 예외 발생
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> authService.withdrawal(memberId));
    }

    @Test
    @DisplayName("회원 탈퇴 실패 테스트 - 이미 탈퇴된 회원")
    void withdrawalFail_AlreadyDeletedMember() {
        // Given: 이미 탈퇴된 회원 ID
        Long memberId = 1L;
        Member deletedMember = new Member();
        deletedMember.setId(memberId);
        deletedMember.setIsDeleted(true); // 탈퇴 상태 설정

        // Mocking: 회원 조회 시 탈퇴된 회원 객체 반환
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(deletedMember));

        // When & Then: 이미 탈퇴된 계정인 경우 예외 발생
        assertThrows(AccountDeletedException.class, () -> authService.withdrawal(memberId));
    }

}