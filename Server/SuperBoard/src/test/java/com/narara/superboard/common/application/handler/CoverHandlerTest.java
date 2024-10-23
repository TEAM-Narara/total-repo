package com.narara.superboard.common.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import com.narara.superboard.common.exception.cover.NotFoundCoverValueException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


class CoverHandlerTest {

    @InjectMocks
    private CoverHandler coverHandler;

    @Mock
    private CoverValidator coverValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // MethodSource로 Map 데이터를 제공
    static Stream<Map<String, Object>> provideInvalidCovers() {
        Map<String, Object> coverWithoutType = new HashMap<>();
        coverWithoutType.put("value", "#ffffff");

        Map<String, Object> coverWithNullType = new HashMap<>();
        coverWithNullType.put("type", null);
        coverWithNullType.put("value", "#ffffff");

        return Stream.of(coverWithoutType, coverWithNullType);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCovers")
    @DisplayName("커버의 타입이 존재하지 않으면 예외가 발생한다.")
    void testGetTypeFailure(Map<String, Object> invalidCover) {
        // when & then
        doThrow(new NotFoundCoverTypeException()).when(coverValidator).validateCoverTypeIsEmpty(invalidCover);

        assertThrows(NotFoundCoverTypeException.class, () ->
            coverHandler.getType(invalidCover)
        );

        verify(coverValidator, times(1)).validateCoverTypeIsEmpty(invalidCover);
    }

    // MethodSource로 Map 데이터를 제공
    static Stream<Map<String, Object>> provideValidCovers() {
        Map<String, Object> cover1 = new HashMap<>();
        cover1.put("type", "COLOR");
        cover1.put("value", "#ffffff");

        Map<String, Object> cover2 = new HashMap<>();
        cover2.put("type", "IMAGE");
        cover2.put("value", "https://example.com/image.jpg");

        return Stream.of(cover1, cover2);
    }

    @ParameterizedTest
    @MethodSource("provideValidCovers")
    @DisplayName("커버의 타입이 정상적으로 반환되는지 테스트")
    void testGetTypeSuccess(Map<String, Object> validCover) {
        String type = validCover.get("type").toString();

        // when: CoverValidator 메서드들이 제대로 호출되는지 설정
        doNothing().when(coverValidator).validateCoversEmpty(validCover);
        doNothing().when(coverValidator).validateCoverTypeIsEmpty(validCover);
        doNothing().when(coverValidator).validateCoverTypeIsValid(type);

        // then: CoverHandler의 getType 메서드가 제대로 커버 타입을 반환하는지 검증
        CoverType result = coverHandler.getType(validCover);
        assertEquals(CoverType.valueOf(type.toUpperCase()), result);

        // 검증: 메서드들이 한 번씩 호출되었는지 확인
        verify(coverValidator, times(1)).validateCoversEmpty(validCover);
        verify(coverValidator, times(1)).validateCoverTypeIsEmpty(validCover);
        verify(coverValidator, times(1)).validateCoverTypeIsValid(type);
    }

    @Test
    @DisplayName("커버의 타입 값이 정상적으로 반환되는지 테스트")
    void testGetTypeValueSuccess() {
        // given
        Map<String, Object> validCover = new HashMap<>();
        validCover.put("type", "COLOR");
        validCover.put("value", "#ffffff");

        // when: void 메서드를 모킹할 때는 doNothing()을 사용
        doNothing().when(coverValidator).validateCoversEmpty(validCover);
        doNothing().when(coverValidator).validateCoverTypeIsEmpty(validCover);
        doNothing().when(coverValidator).validateCoverTypeIsValid("COLOR");

        // then
        String result = coverHandler.getTypeValue(validCover);
        assertEquals(CoverType.COLOR.getValue(), result);  // CoverType.COLOR의 value와 동일한지 확인

        // 메서드가 제대로 호출되었는지 검증
        verify(coverValidator, times(1)).validateCoversEmpty(validCover);
        verify(coverValidator, times(1)).validateCoverTypeIsEmpty(validCover);
        verify(coverValidator, times(1)).validateCoverTypeIsValid("COLOR");
    }

    @ParameterizedTest
    @DisplayName("커버의 값이 없을 때 예외가 발생하는지 테스트")
    @MethodSource("provideInvalidCoversByInvalidValue")
    void testGetValueFailure(Map<String, Object> invalidCover) {
        // given: invalidCover는 "value" 값이 존재하지 않음

        // when: 커버가 비어 있거나 "value" 값이 없을 경우 예외를 던짐
        doThrow(new NotFoundCoverValueException()).when(coverValidator).validateCoverValueIsEmpty(invalidCover);

        // then: NotFoundCoverValueException이 발생해야 함
        assertThrows(NotFoundCoverValueException.class, () -> coverHandler.getValue(invalidCover));

        // verify: 필요한 검증 로직
        verify(coverValidator, times(1)).validateCoversEmpty(invalidCover);
        verify(coverValidator, times(1)).validateCoverValueIsEmpty(invalidCover);
    }

    // MethodSource로 제공할 잘못된 Map들
    static Stream<Map<String, Object>> provideInvalidCoversByInvalidValue() {
        Map<String, Object> coverWithoutValue = new HashMap<>();
        coverWithoutValue.put("type", "COLOR");

        Map<String, Object> coverWithNullValue = new HashMap<>();
        coverWithNullValue.put("type", "IMAGE");
        coverWithNullValue.put("value", "");

        return Stream.of(coverWithoutValue, coverWithNullValue);
    }

}
