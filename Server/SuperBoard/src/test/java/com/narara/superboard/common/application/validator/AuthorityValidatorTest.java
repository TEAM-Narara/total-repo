package com.narara.superboard.common.application.validator;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.common.exception.authority.InvalidAuthorityFormatException;
import com.narara.superboard.common.exception.authority.NotFoundAuthorityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthorityValidator 테스트")
class AuthorityValidatorTest implements MockSuperBoardUnitTests {

    @InjectMocks
    private AuthorityValidator authorityValidator;

    @Test
    @DisplayName("유효하지 않은 권한 값 전달 시 InvalidAuthorityFormatException 발생")
    void testInvalidAuthorityValue() {
        // given
        String invalidAuthority = "INVALID";  // 유효하지 않은 권한 값

        // when & then
        assertThrows(InvalidAuthorityFormatException.class, () -> authorityValidator.validateAuthorityTypeIsValid(invalidAuthority));
    }

    @ParameterizedTest
    @DisplayName("비어있는 권한 타입이 전달될 경우 NotFoundAuthorityException 예외 발생")
    @CsvSource({
            "'',",    // 빈 문자열
            "null"    // null 값
    })
    void testValidateAuthorityTypeIsValidFailure(String invalidAuthority) {
        // 권한 값이 null인 경우 처리
        String authority = "null".equals(invalidAuthority) ? null : invalidAuthority;

        // NotFoundAuthorityException 예외 발생 검증
        assertThrows(NotFoundAuthorityException.class, () -> authorityValidator.validateAuthorityTypeIsValid(authority));
    }

    @ParameterizedTest
    @DisplayName("유효한 권한 타입이 전달될 경우 예외 발생하지 않음")
    @ValueSource(strings = {"ADMIN", "MEMBER"})
    void testValidateAuthorityTypeIsValidSuccess(String validAuthority) {
        // 그린사이클: 유효한 권한 값일 때 예외가 발생하지 않음
        assertDoesNotThrow(() -> authorityValidator.validateAuthorityTypeIsValid(validAuthority));
    }
}