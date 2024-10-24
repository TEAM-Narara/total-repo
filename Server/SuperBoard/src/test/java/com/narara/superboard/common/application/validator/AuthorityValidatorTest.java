package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.exception.authority.InvalidAuthorityFormatException;
import com.narara.superboard.common.exception.authority.NotFoundAuthorityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthorityValidator 테스트")
class AuthorityValidatorTest {

    @InjectMocks
    private AuthorityValidator authorityValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("유효하지 않은 권한 값 전달 시 InvalidAuthorityFormatException 발생")
    void testInvalidAuthorityValue() {
        // given
        String invalidAuthority = "INVALID";  // 유효하지 않은 권한 값

        // when & then
        assertThrows(InvalidAuthorityFormatException.class, () -> authorityValidator.validateAuthorityTypeIsValid(invalidAuthority));
    }
}