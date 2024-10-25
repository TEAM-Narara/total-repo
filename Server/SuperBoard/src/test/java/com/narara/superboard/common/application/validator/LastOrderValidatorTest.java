package com.narara.superboard.common.application.validator;

import static org.junit.jupiter.api.Assertions.*;

import com.narara.superboard.common.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("마지막 순서 검증에 대한 단위 테스트")
class LastOrderValidatorTest{

    private LastOrderValidator lastOrderValidator;

    @BeforeEach
    void setUp() {
        lastOrderValidator = new LastOrderValidator();
    }

    @DisplayName("리스트의 마지막 순서 값이 없으면 에러 발생")
    @Test
    void testCheckValidLastOrder_Failure() {
        // 실패 로직: lastOrder가 null일 때 NotFoundException 발생
        Long nullLastOrder = null;

        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> lastOrderValidator.checkValidListLastOrder(nullLastOrder)
        );

        assertEquals("보드에서 리스트의 마지막 순서값을 DB 내에서 찾을 수 없습니다.", exception.getMessage());
    }

    @DisplayName("리스트의 마지막 순서 값이 있으면 정상 작동")
    @Test
    public void testCheckValidLastOrder_Success() {
        // 성공 로직: lastOrder가 null이 아닐 때 예외가 발생하지 않음
        Long validLastOrder = 1L;

        // 예외가 발생하지 않으므로 assertThrows는 사용하지 않음
        assertDoesNotThrow(() -> lastOrderValidator.checkValidListLastOrder(validLastOrder));
    }
}
