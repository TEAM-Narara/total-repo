package com.narara.superboard.common.application.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.cover.InvalidCoverTypeFormatException;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;


@DisplayName("CoverValidator 테스트")
class CoverValidatorTest {

    @InjectMocks
    private CoverValidator coverValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("커버 타입이 null일 경우 NotFoundCoverTypeException 발생")
    void testCoverTypeIsNull() {
        // null 값이 전달될 때 예외 발생 테스트
        assertThrows(NotFoundCoverTypeException.class, () -> {
            coverValidator.validateCoverTypeIsValid(null);
        });
    }

    @Test
    @DisplayName("커버 타입이 빈 문자열일 경우 NotFoundCoverTypeException 발생")
    void testCoverTypeIsEmpty() {
        // 빈 문자열이 전달될 때 예외 발생 테스트
        assertThrows(NotFoundCoverTypeException.class, () -> {
            coverValidator.validateCoverTypeIsValid("");
        });
    }

    @Test
    @DisplayName("유효하지 않은 커버 타입일 경우 InvalidCoverTypeFormatException 발생")
    void testInvalidCoverType() {
        // 잘못된 커버 타입 값이 전달될 때 예외 발생 테스트
        assertThrows(InvalidCoverTypeFormatException.class, () -> {
            coverValidator.validateCoverTypeIsValid("INVALID_TYPE");
        });
    }

}
