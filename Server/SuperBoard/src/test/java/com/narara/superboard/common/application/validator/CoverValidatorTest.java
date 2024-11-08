package com.narara.superboard.common.application.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.narara.superboard.common.interfaces.dto.CoverDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.cover.InvalidCoverTypeFormatException;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import com.narara.superboard.common.exception.cover.NotFoundCoverValueException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;


@DisplayName("커버 검증에 대한 단위 테스트")
class CoverValidatorTest {

    private CoverValidator coverValidator;

    @BeforeEach
    public void setUp() {
        coverValidator = new CoverValidator();
    }

    @Test
    @DisplayName("커버 타입이 null일 경우 NotFoundCoverTypeException 발생")
    void testCoverTypeIsNull() {
        // null 값이 전달될 때 예외 발생 테스트
        assertThrows(NotFoundCoverTypeException.class, () -> {
            coverValidator.validateCoverTypeIsValid(new CoverDto(null , "https://"));
        });
    }

    @Test
    @DisplayName("커버 타입이 빈 문자열일 경우 NotFoundCoverTypeException 발생")
    void testCoverTypeIsEmpty() {
        // 빈 문자열이 전달될 때 예외 발생 테스트
        assertThrows(InvalidCoverTypeFormatException.class, () -> {
            coverValidator.validateCoverTypeIsValid(
                    new HashMap<>(){{
                        put("type", "");
                    }}
            );
        });
    }

    @Test
    @DisplayName("유효하지 않은 커버 타입일 경우 InvalidCoverTypeFormatException 발생")
    void testInvalidCoverType() {
        // 잘못된 커버 타입 값이 전달될 때 예외 발생 테스트
        assertThrows(InvalidCoverTypeFormatException.class, () -> {
            coverValidator.validateCoverTypeIsValid(
                    new HashMap<>(){{
                        put("type", "INVALID_TYPE");
                    }}
            );
        });
    }

    @ParameterizedTest
    @EnumSource(CoverType.class) // CoverType Enum의 모든 값을 테스트
    @DisplayName("유효한 커버 타입일 경우 예외 발생하지 않음")
    void testValidCoverType(CoverType coverType) {
        // 유효한 커버 타입 값이 전달될 때 예외가 발생하지 않는지 테스트
        assertDoesNotThrow(() -> coverValidator.validateCoverTypeIsValid(
                new HashMap<>(){{
                    put("type", coverType.toString());
                }}
        ));
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

    private static Stream<Arguments> provideMissingTypeCases() {
        return Stream.of(
                Arguments.of(Map.of("value", "#FFFFFF")),
                Arguments.of(Map.of("value", "https://example.com/image.png")),
                Arguments.of(Map.of("value", "linear-gradient(#e66465, #9198e5)"))
        );
    }
    // 실패 테스트: type이 없는 경우
    @ParameterizedTest
    @MethodSource("provideMissingTypeCases")
    @DisplayName("실패 케이스: 'type'이 없는 경우 NotFoundCoverTypeException 발생")
    void validateCardCover_Failure_NoType(Map<String, Object> cover) {
        // given
        CardUpdateRequestDto requestDto = new CardUpdateRequestDto(
                "Test Card",
                "Test Description",
                null,
                null,
                new CoverDto((String)cover.get("type"), (String)cover.get("value"))
        );

        // when & then
        assertThrows(NotFoundCoverTypeException.class, () -> coverValidator.validateCoverTypeIsValid(requestDto.cover()));
    }

    // Test data for missing value cases
    private static Stream<Arguments> provideMissingValueCases() {
        return Stream.of(
                Arguments.of(Map.of("type", "COLOR")),
                Arguments.of(Map.of("type", "IMAGE"))
        );
    }
    // 실패 테스트: value가 없는 경우
    @ParameterizedTest
    @MethodSource("provideMissingValueCases")
    @DisplayName("실패 케이스: 'value'가 없는 경우 NotFoundCoverValueException 발생")
    void validateCardCover_Failure_NoValue(Map<String, Object> cover) {
        // given
        CardUpdateRequestDto requestDto = new CardUpdateRequestDto(
                "Test Card",
                "Test Description",
                null,
                null,
                new CoverDto((String)cover.get("type"), (String)cover.get("value"))
        );

        // when & then
        assertThrows(NotFoundCoverValueException.class, () -> coverValidator.validateCoverTypeIsValid(requestDto.cover()));
    }

    // 유효하지 않은 type 값 케이스
    private static Stream<Arguments> provideInvalidTypeCases() {
        return Stream.of(
                Arguments.of(Map.of("type", "INVALID_TYPE", "value", "#FFFFFF")),
                Arguments.of(Map.of("type", "WRONG_IMAGE", "value", "https://example.com/image.png")),
                Arguments.of(Map.of("type", "UNKNOWN", "value", "linear-gradient(#e66465, #9198e5)"))
        );
    }


    // 실패 테스트: type이 유효하지 않은 경우
    @ParameterizedTest
    @MethodSource("provideInvalidTypeCases")
    @DisplayName("실패 케이스: cover의 'type'이 유효하지 않은 경우 InvalidCoverTypeFormatException 발생")
    void validateCardCover_Failure_InvalidType(Map<String, Object> cover) {
        // given
        CardUpdateRequestDto requestDto = new CardUpdateRequestDto(
                "Test Card",
                "Test Description",
                null,
                null,
                new CoverDto((String)cover.get("type"), (String)cover.get("value"))
        );

        // when & then
        assertThrows(InvalidCoverTypeFormatException.class, () -> coverValidator.validateCoverTypeIsValid(requestDto.cover()));
    }

    // 유효한 cover 케이스
    private static Stream<Arguments> provideValidCoverCases() {
        return Stream.of(
                Arguments.of(Map.of("type", "COLOR", "value", "#FFFFFF")),
                Arguments.of(Map.of("type", "IMAGE", "value", "https://example.com/image.png"))
//                Arguments.of(Map.of("type", "GRADIENT", "value", "linear-gradient(#e66465, #9198e5)"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidCoverCases")
    @DisplayName("성공 케이스: cover의 'type'과 'value'가 올바른 경우")
    void validateCardCover_Success(Map<String, Object> cover) {
        // given
        CardUpdateRequestDto requestDto = new CardUpdateRequestDto(
                "Test Card",
                "Test Description",
                null,
                null,
                new CoverDto((String)cover.get("type"), (String)cover.get("value"))
        );

        // when & then
        assertDoesNotThrow(() -> coverValidator.validateCoverTypeIsValid(requestDto.cover()));
    }
}
