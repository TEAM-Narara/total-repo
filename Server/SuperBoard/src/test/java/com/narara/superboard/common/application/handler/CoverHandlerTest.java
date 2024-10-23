package com.narara.superboard.common.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

        assertThrows(NotFoundCoverTypeException.class, () -> {
            coverHandler.getType(invalidCover);
        });

        verify(coverValidator, times(1)).validateCoverTypeIsEmpty(invalidCover);
    }
}
