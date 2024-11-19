package com.narara.superboard.common.application.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.narara.superboard.board.entity.Board;
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

    @DisplayName("보드에서 리스트의 마지막 순서 값이 없으면 에러 발생")
    @Test
    void testCheckValidListLastOrder_Failure() {
        // given: Board 엔티티가 있고, 마지막 리스트 순서가 null일 때
        Board board = mock(Board.class);
        when(board.getLastListOrder()).thenReturn(null);

        // when & then: 예외가 발생하는지 확인
        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> lastOrderValidator.checkValidListLastOrder(board)
        );

        assertEquals("보드에서 리스트의 마지막 순서값을 DB 내에서 찾을 수 없습니다.", exception.getMessage());
    }

    @DisplayName("보드에서 리스트의 마지막 순서 값이 있으면 정상 작동")
    @Test
    public void testCheckValidListLastOrder_Success() {
        // given: Board 엔티티가 있고, 마지막 리스트 순서가 유효할 때
        Board board = mock(Board.class);
        when(board.getLastListOrder()).thenReturn(1L);

        // when & then: 예외가 발생하지 않음을 확인
        assertDoesNotThrow(() -> lastOrderValidator.checkValidListLastOrder(board));
    }

}
