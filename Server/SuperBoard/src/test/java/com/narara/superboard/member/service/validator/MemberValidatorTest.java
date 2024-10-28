package com.narara.superboard.member.service.validator;

import com.narara.superboard.member.exception.*;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberValidatorTest {
    private MemberValidator memberValidator;

    @BeforeEach
     void setUp() {
        // MemberValidator 초기화
        memberValidator = new MemberValidator();
    }

    // 이메일 검증 테스트 - 이메일이 없을 때 예외 발생
    @Test
    @DisplayName("이메일 검증 테스트 - 이메일이 없을 때 예외 발생")
    void validateEmail_throwsExceptionWhenEmailIsEmpty() {
        // Given: 빈 이메일
        String email = null;

        // When & Then: 이메일이 없을 때 MemberEmailNotFoundException 예외가 발생하는지 확인
        assertThrows(MemberEmailNotFoundException.class, () -> memberValidator.registerValidate(new MemberCreateRequestDto(email, "nickname", "password123")));
    }

    // 이메일 검증 테스트 - 잘못된 이메일 형식일 때 예외 발생
    @Test
    @DisplayName("이메일 검증 테스트 - 잘못된 이메일 형식일 때 예외 발생")
    void validateEmail_throwsExceptionWhenEmailFormatIsInvalid() {
        // Given: 잘못된 이메일 형식
        String email = "invalid-email";

        // When & Then: 잘못된 이메일 형식일 때 MemberInvalidEmailFormatException 예외가 발생하는지 확인
        assertThrows(MemberInvalidEmailFormatException.class, () -> memberValidator.registerValidate(new MemberCreateRequestDto(email, "nickname", "password123")));
    }

    // 닉네임 검증 테스트 - 닉네임이 없을 때 예외 발생
    @Test
    @DisplayName("닉네임 검증 테스트 - 닉네임이 없을 때 예외 발생")
    void validateNickname_throwsExceptionWhenNicknameIsEmpty() {
        // Given: 빈 닉네임
        String nickname = "";

        // When & Then: 닉네임이 없을 때 MemberNicknameNotFoundException 예외가 발생하는지 확인
        assertThrows(MemberNicknameNotFoundException.class, () -> memberValidator.registerValidate(new MemberCreateRequestDto("test@test.com", nickname, "password123")));
    }

    // 비밀번호 검증 테스트 - 비밀번호가 없을 때 예외 발생
    @Test
    @DisplayName("비밀번호 검증 테스트 - 비밀번호가 없을 때 예외 발생")
    void validatePassword_throwsExceptionWhenPasswordIsEmpty() {
        // Given: 빈 비밀번호
        String password = "";

        // When & Then: 비밀번호가 없을 때 MemberPasswordNotFoundException 예외가 발생하는지 확인
        assertThrows(MemberPasswordNotFoundException.class, () -> memberValidator.registerValidate(new MemberCreateRequestDto("test@test.com", "nickname", password)));
    }

    // 비밀번호 검증 테스트 - 비밀번호 길이가 너무 짧을 때 예외 발생
    @Test
    @DisplayName("비밀번호 검증 테스트 - 비밀번호가 너무 짧을 때 예외 발생")
    void validatePassword_throwsExceptionWhenPasswordTooShort() {
        // Given: 짧은 비밀번호
        String password = "abc"; // 3자리 비밀번호

        // When & Then: 비밀번호가 너무 짧을 때 MemberInvalidPasswordFormatException 예외가 발생하는지 확인
        assertThrows(MemberInvalidPasswordFormatException.class, () -> memberValidator.registerValidate(new MemberCreateRequestDto("test@test.com", "nickname", password)));
    }

    // 비밀번호 검증 테스트 - 비밀번호 길이가 너무 길 때 예외 발생
    @Test
    @DisplayName("비밀번호 검증 테스트 - 비밀번호가 너무 길 때 예외 발생")
    void validatePassword_throwsExceptionWhenPasswordTooLong() {
        // Given: 너무 긴 비밀번호 (31자리)
        String password = "a".repeat(31); // 31자리 비밀번호

        // When & Then: 비밀번호가 너무 길 때 MemberInvalidPasswordFormatException 예외가 발생하는지 확인
        assertThrows(MemberInvalidPasswordFormatException.class, () -> memberValidator.registerValidate(new MemberCreateRequestDto("test@test.com", "nickname", password)));
    }

    // 정상적인 경우 테스트 - 모든 값이 정상일 때 예외가 발생하지 않는지 확인
    @Test
    @DisplayName("회원가입 검증 테스트 - 모든 값이 정상일 때 예외 발생하지 않음")
    void registerValidate_successfulValidation() {
        // Given: 정상적인 회원가입 요청 DTO
        MemberCreateRequestDto requestDto = new MemberCreateRequestDto("test@test.com", "nickname", "password123");

        // When & Then: 예외가 발생하지 않는지 확인
        memberValidator.registerValidate(requestDto);
    }
}