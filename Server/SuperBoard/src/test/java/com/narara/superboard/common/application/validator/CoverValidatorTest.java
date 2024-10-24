package com.narara.superboard.common.application.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.cover.InvalidCoverTypeFormatException;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import com.narara.superboard.common.exception.cover.NotFoundCoverValueException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;


@DisplayName("CoverValidator 테스트")
class CoverValidatorTest implements MockSuperBoardUnitTests {

    @InjectMocks
    private CoverValidator coverValidator;

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
        assertDoesNotThrow(() -> coverValidator.validateCoverTypeIsValid(coverType.toString()));
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

    @Test
    @DisplayName("커버가 유효할 때 예외가 발생하지 않는다")
    void testValidateCoversNotEmpty() {
        // 유효한 커버 맵 데이터
        Map<String, Object> validCover = Map.of("type", "color", "value", "#ffffff");

        // 예외 발생하지 않음
        coverValidator.validateCoversEmpty(validCover);
    }

    @DisplayName("커버에 'type'이 없으면 NotFoundCoverTypeException이 발생한다")
    @ParameterizedTest
    @MethodSource("provideInvalidCoverDataByInvalidType")
    void testValidateCoverTypeIsEmpty(Map<String, Object> cover) {
        assertThrows(NotFoundCoverTypeException.class, () -> coverValidator.validateCoverTypeIsEmpty(cover));
    }

    static Stream<Arguments> provideInvalidCoverDataByInvalidType() {
        return Stream.of(
                Arguments.of(Map.of()),  // 빈 맵
                Arguments.of(Map.of("value", "#ffffff"))  // 'type' 키가 없는 경우
        );
    }

    @DisplayName("커버에 'type'이 있을 때 예외가 발생하지 않는다")
    @ParameterizedTest
    @MethodSource("provideValidCoverData")
    void testValidateCoverTypeIsNotEmpty(Map<String, Object> cover) {
        coverValidator.validateCoverTypeIsEmpty(cover);  // 예외 발생하지 않음
    }

    static Stream<Arguments> provideValidCoverData() {
        return Stream.of(
                Arguments.of(Map.of("type", "color", "value", "#ffffff")),  // 정상적인 커버 데이터
                Arguments.of(Map.of("type", "image", "value", "https://example.com/image.jpg"))  // 정상적인 커버 데이터
        );
    }

    @DisplayName("커버에 'value'가 없으면 NotFoundCoverValueException 발생")
    @ParameterizedTest
    @MethodSource("provideCoversForTest")
    void testValidateCoverValueIsEmpty(Map<String, Object> cover) {
        // 예외 발생 여부 테스트
        assertThrows(NotFoundCoverValueException.class, () -> coverValidator.validateCoverValueIsEmpty(cover));
    }

    static Stream<Arguments> provideCoversForTest() {
        // cover에 'value' 키가 없는 경우
        Map<String, Object> coverWithoutValue = new HashMap<>();
        coverWithoutValue.put("type", "COLOR");

        // 빈 cover
        Map<String, Object> emptyCover = new HashMap<>();

        return Stream.of(
                Arguments.of(coverWithoutValue),
                Arguments.of(emptyCover)
        );
    }

    @DisplayName("커버에 'value'가 있을 경우 예외가 발생하지 않음")
    @ParameterizedTest
    @MethodSource("provideValidCoversForTest")
    void testValidateCoverValueIsNotEmpty(Map<String, Object> cover) {
        // 예외가 발생하지 않는지 테스트
        assertDoesNotThrow(() -> coverValidator.validateCoverValueIsEmpty(cover));
    }

    static Stream<Arguments> provideValidCoversForTest() {
        // cover에 'value' 키가 있는 경우
        Map<String, Object> validCoverWithColor = new HashMap<>();
        validCoverWithColor.put("type", "COLOR");
        validCoverWithColor.put("value", "#ffffff");

        Map<String, Object> validCoverWithImage = new HashMap<>();
        validCoverWithImage.put("type", "IMAGE");
        validCoverWithImage.put("value", "https://example.com/image.jpg");

        return Stream.of(
                Arguments.of(validCoverWithColor),
                Arguments.of(validCoverWithImage)
        );
    }
}
