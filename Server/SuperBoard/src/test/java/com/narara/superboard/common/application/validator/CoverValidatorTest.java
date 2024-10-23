package com.narara.superboard.common.application.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.cover.InvalidCoverTypeFormatException;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @EnumSource(CoverType.class) // CoverType Enum의 모든 값을 테스트
    @DisplayName("유효한 커버 타입일 경우 예외 발생하지 않음")
    void testValidCoverType(CoverType coverType) {
        // 유효한 커버 타입 값이 전달될 때 예외가 발생하지 않는지 테스트
        assertDoesNotThrow(() -> coverValidator.validateCoverTypeIsValid(coverType.getValue()));
    }

    @DisplayName("커버가 null이거나 비어 있을 때 NotFoundException이 발생한다")
    @ParameterizedTest
    @MethodSource("provideInvalidCoverData")
    void testValidateCoversEmpty(Map<String, Object> cover) {
        assertThrows(NotFoundException.class, () -> coverValidator.validateCoversEmpty(cover));
    }

    static Stream<Map<String, Object>> provideInvalidCoverData() {
        return Stream.of(
                Map.of(), // 빈 Map
                null // null인 경우
        );
    }

}
