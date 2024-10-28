package com.narara.superboard.member.service;

import com.narara.superboard.common.exception.redis.RedisDataNotFoundException;
import com.narara.superboard.common.exception.redis.RedisDataSaveException;
import com.narara.superboard.common.infrastructure.redis.RedisServiceImpl;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.enums.LoginType;
import com.narara.superboard.member.exception.*;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;
import com.narara.superboard.member.service.validator.MemberValidator;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberValidator memberValidator;

    @InjectMocks
    private EmailServiceImpl emailService; // 테스트 대상 서비스

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
     * 이메일 인증코드 전송 TEST ------------------------------------------------------------------------
     */
    @Test
    @DisplayName("이메일 인증코드 전송 성공 테스트")
    void sendEmailVerificationCodeSuccess() throws Exception {
        // Given: 존재하는 이메일과 인증 코드
        String email = "test@example.com";
        String verificationCode = "123456"; // 가정된 인증 코드

        // Redis에 기존 데이터가 없는 경우 (새로 등록하는 상황)
        when(redisService.existData(email)).thenReturn(false);

        // 이메일 전송과 관련된 모든 동작이 정상 동작할 것으로 가정
        MimeMessage mimeMessage = mock(MimeMessage.class); // 목 객체로 MimeMessage 생성
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        // 템플릿 엔진이 정상적으로 데이터를 처리하도록 설정
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("email content here");

        // When: 이메일 인증 코드 전송 시도
        emailService.sendEmailVerificationCode(email);

        // Then: 이메일이 성공적으로 Redis에 저장되고 이메일이 전송되었는지 확인
        verify(redisService, times(1)).setDataExpire(eq(email), anyString(), anyLong()); // Redis에 인증 코드가 저장되었는지 확인
        verify(emailSender, times(1)).send(mimeMessage); // 이메일 전송이 정상적으로 호출되었는지 확인
    }

    @Test
    @DisplayName("이메일 인증코드 전송 실패 테스트 - 이메일이 null인 경우")
    void sendEmailVerificationCodeFail_NullEmail() {
        // Given: null 이메일
        String email = null;

        // When & Then: 이메일이 null일 때 예외 발생 확인
        doThrow(new MemberEmailNotFoundException()).when(memberValidator).validateEmail(email);
        assertThrows(MemberEmailNotFoundException.class, () -> emailService.sendEmailVerificationCode(email));

        // Redis나 이메일 전송 시도되지 않음을 확인
        verify(redisService, never()).setDataExpire(anyString(), anyString(), anyLong());
        verify(emailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 인증코드 전송 성공 테스트 - 이미 존재하는 이메일")
    void sendEmailVerificationCodeSuccess_ExistingEmail() throws Exception {
        // Given: 이미 Redis에 저장된 이메일
        String email = "test@example.com";
        String verificationCode = "123456"; // 가정된 인증 코드

        // Redis에 기존 데이터가 존재하는 경우 (삭제 후 새로 등록하는 상황)
        when(redisService.existData(email)).thenReturn(true);

        // 이메일 전송과 관련된 모든 동작이 정상 동작할 것으로 가정
        MimeMessage mimeMessage = mock(MimeMessage.class); // 목 객체로 MimeMessage 생성
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("email content here");

        // When: 이메일 인증 코드 전송 시도
        emailService.sendEmailVerificationCode(email);

        // Then: Redis에서 기존 데이터가 삭제되고 새로 저장된 후, 이메일이 정상적으로 전송되었는지 확인
        verify(redisService, times(1)).deleteData(email); // Redis에 기존 데이터가 삭제되었는지 확인
        verify(redisService, times(1)).setDataExpire(eq(email), anyString(), anyLong()); // Redis에 새 인증 코드가 저장되었는지 확인
        verify(emailSender, times(1)).send(mimeMessage); // 이메일 전송이 정상적으로 호출되었는지 확인
    }

    @Test
    @DisplayName("이메일 인증코드 전송 실패 테스트 - 메일 서버 오류")
    void sendEmailVerificationCodeFail_MailServerError() throws Exception {
        // Given: 이메일과 인증 코드
        String email = "test@example.com";

        // Redis에 데이터가 없는 경우 (새로 등록)
        when(redisService.existData(email)).thenReturn(false);

        // 이메일 전송 중 예외 발생
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("email content here");

        // When: 이메일 전송 시도할 때
        doThrow(new RuntimeException("메일 서버 오류")).when(emailSender).send(mimeMessage);

        // When & Then: 이메일 전송 중 예외 발생 확인
        assertThrows(RuntimeException.class, () -> emailService.sendEmailVerificationCode(email));

        // 이메일 전송 시도 확인
        verify(emailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("이메일 인증코드 전송 실패 테스트 - Redis 저장 실패")
    void sendEmailVerificationCodeFail_RedisSaveError() throws Exception {
        // Given: 이메일과 인증 코드
        String email = "test@example.com";

        // Redis에 데이터가 없는 경우 (새로 등록)
        when(redisService.existData(email)).thenReturn(false);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("email content here");

        // 이메일 전송을 위한 MimeMessage 생성
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Redis에 데이터를 저장하는 중 예외 발생 설정
        doThrow(new RedisDataSaveException(email, "123456")).when(redisService).setDataExpire(eq(email), anyString(), anyLong());

        // When & Then: Redis 저장 중 예외 발생 확인
        // 이메일 인증 코드 전송 시도 시 Redis 저장 오류가 발생해야 함
        assertThrows(RedisDataSaveException.class, () -> emailService.sendEmailVerificationCode(email));

        // 이메일 전송은 시도되지 않아야 함
        verify(emailSender, never()).send(any(MimeMessage.class));
    }


    @Test
    @DisplayName("이메일 인증코드 전송 실패 테스트 - 잘못된 이메일 형식")
    void sendEmailVerificationCodeFail_InvalidEmailFormat() {
        // Given: 잘못된 이메일 형식
        String email = "invalid-email-format";

        // When & Then: 이메일 형식이 잘못되었을 때 예외 발생
        doThrow(new MemberInvalidEmailFormatException()).when(memberValidator).validateEmail(email);

        assertThrows(MemberInvalidEmailFormatException.class, () -> emailService.sendEmailVerificationCode(email));

        // Redis나 이메일 전송 시도되지 않음을 확인
        verify(redisService, never()).setDataExpire(anyString(), anyString(), anyLong());
        verify(emailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 인증코드 전송 실패 테스트 - 중복 이메일")
    void sendEmailVerificationCodeFail_ExistingEmail_AccountDeleted() {
        // Given: 존재하는 이메일과 탈퇴한 사용자
        String email = "test@example.com";
        Member deletedMember = new Member(1L, "testNickname", email, "encodedPassword", "");
        deletedMember.setIsDeleted(true); // 계정 탈퇴 상태 설정

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(deletedMember));

        // When & Then: 탈퇴한 계정인 경우 예외 발생
        assertThrows(AccountDeletedException.class, () -> emailService.sendEmailVerificationCode(email));

        // Redis나 이메일 전송 시도되지 않음을 확인
        verify(redisService, never()).setDataExpire(anyString(), anyString(), anyLong());
        verify(emailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 인증코드 전송 실패 테스트 - 중복 이메일, 로그인 유형 체크")
    void sendEmailVerificationCodeFail_ExistingEmail_LoginTypeCheck() {
        // Given: 존재하는 이메일과 탈퇴하지 않은 사용자
        String email = "test@example.com";
        Member existingMember = new Member(1L, "testNickname", email, "encodedPassword", "");
        existingMember.setIsDeleted(false); // 계정이 활성 상태
        existingMember.setLoginType(LoginType.NAVER); // 로그인 유형 설정

        // DB에서 해당 이메일 찾기
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existingMember));

        // When & Then: 특정 로그인 유형에서 예외 발생 확인
        assertThrows(AlreadyRegisteredLoginException.class, () -> emailService.sendEmailVerificationCode(email));

        // Redis나 이메일 전송 시도되지 않음을 확인
        verify(redisService, never()).setDataExpire(anyString(), anyString(), anyLong());
        verify(emailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * 이메일 인증코드 인증 TEST ------------------------------------------------------------------------
     */

    @Test
    @DisplayName("이메일 인증 코드 검증 성공 테스트")
    void verifyEmailCodeSuccess() {
        // Given
        String email = "test@example.com";
        String code = "123456";
        VerifyEmailCodeRequestDto requestDto = new VerifyEmailCodeRequestDto(email, code);

        // Redis에 저장된 인증 코드
        when(redisService.getData(email)).thenReturn(code); // Redis에서 올바른 코드 반환

        // When
        emailService.verifyEmailCode(requestDto);

        // Then
        verify(redisService, times(1)).deleteData(email); // 인증 성공 후 Redis에서 코드 삭제 확인
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 실패 테스트 - Redis에 코드 없음")
    void verifyEmailCodeFail_RedisDataNotFound() {
        // Given
        String email = "test@example.com";
        String code = "123456";
        VerifyEmailCodeRequestDto requestDto = new VerifyEmailCodeRequestDto(email, code);

        // Redis에 코드가 없음
        when(redisService.getData(email)).thenReturn(null); // Redis에서 null 반환
        // Redis에서 getData 호출 시 예외 발생
        doThrow(new RedisDataNotFoundException(email))
                .when(redisService).getData(email); // Redis에서 코드가 없을 때 예외 발생 설정

        // When & Then: Redis에 코드가 없을 경우 예외 발생
        assertThrows(RedisDataNotFoundException.class, () -> emailService.verifyEmailCode(requestDto));
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 실패 테스트 - 코드가 null인 경우")
    void verifyEmailCodeFail_NullCode() {
        // Given
        String email = "test@example.com";
        String code = null; // null 인증 코드
        VerifyEmailCodeRequestDto requestDto = new VerifyEmailCodeRequestDto(email, code);

        doThrow(new MemberVerificationCodeNotFoundException()).when(memberValidator).verifyEmailCodeValidate(requestDto);
        // When & Then: 인증 코드가 null일 경우 예외 발생
        assertThrows(MemberVerificationCodeNotFoundException.class, () -> emailService.verifyEmailCode(requestDto));
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 실패 테스트 - 코드 불일치")
    void verifyEmailCodeFail_CodeMismatch() {
        // Given
        String email = "test@example.com";
        String inputCode = "123456";
        VerifyEmailCodeRequestDto requestDto = new VerifyEmailCodeRequestDto(email, inputCode);

        // Redis에 저장된 코드
        when(redisService.getData(email)).thenReturn("654321"); // 잘못된 코드 반환

        // When & Then: 코드가 일치하지 않을 경우 예외 발생
        assertThrows(InvalidVerificationCodeException.class, () -> emailService.verifyEmailCode(requestDto));
    }

}