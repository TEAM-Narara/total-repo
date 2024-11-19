package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.exception.color.InvalidColorFormatException;
import com.narara.superboard.common.exception.color.NotFoundColorException;
import com.narara.superboard.common.interfaces.dto.ColorHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("색상 검증기에 대한 단위 테스트")
class ColorValidatorTest {

    ColorValidator colorValidator;

    @BeforeEach
    void setUp() {
        colorValidator = new ColorValidator();
    }

    @ParameterizedTest
    @ValueSource(longs = {0xFFFFFFFFL + 1, -1L, 0xFFFFFFFFFFL})
    @DisplayName("라벨 컬러가 유효 범위를 벗어날 때 InvalidColorFormatException 발생")
    void shouldThrowExceptionWhenColorIsOutOfRange(Long color) {
        // given
        String entity = "TestEntity";
        ColorHolder colorHolder = mock(ColorHolder.class);
        when(colorHolder.color()).thenReturn(color);

        // then
        assertThrows(InvalidColorFormatException.class, () -> colorValidator.validateColorIsValid(colorHolder.color(),entity));
    }
}