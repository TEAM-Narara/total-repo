package com.narara.superboard.common.application.validator;

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
    @NullSource
    @DisplayName("색상이 빈 값이면 NotFoundColorException이 발생해야 한다")
    public void testValidateColorIsEmpty_Failure(Long color) {
        String entity = "TestEntity";
        ColorValidator validator = new ColorValidator();

        assertThrows(NotFoundColorException.class, () -> validator.validateColorIsEmpty(color, entity));
    }

    @ParameterizedTest
    @ValueSource(longs = {0x00000000L, 0xFFFFFFFFL, 0x12345678L})
    @DisplayName("유효한 라벨 컬러 값이 주어졌을 때 예외가 발생하지 않음")
    void shouldNotThrowExceptionWhenColorIsValid(Long color) {
        // given
        String entity = "TestEntity";
        ColorHolder colorHolder = mock(ColorHolder.class);
        when(colorHolder.color()).thenReturn(color);

        // then
        assertDoesNotThrow(() -> colorValidator.validateColorIsEmpty(colorHolder.color(),entity));
    }
}