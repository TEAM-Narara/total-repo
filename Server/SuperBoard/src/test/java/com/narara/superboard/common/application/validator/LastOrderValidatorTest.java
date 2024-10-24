package com.narara.superboard.common.application.validator;

import static org.junit.jupiter.api.Assertions.*;

import com.narara.superboard.common.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("마지막 순서에 대해서 검증해주는 검증기의 테스트")
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
}
