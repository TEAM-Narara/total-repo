package com.narara.superboard.cardlabel.service.validator;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.exception.cardlabel.MismatchedBoardException;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.list.entity.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("카드 검증기에 대한 단위 테스트")
class CardLabelValidatorTest {

    private final CardLabelValidator cardLabelValidator = new CardLabelValidator();

    @Test
    @DisplayName("Card와 Label이 다른 Board에 속해 있을 때 MismatchedBoardException 발생")
    void validateMismatchBoard_Failure() {
        // given
        Board board1 = mock(Board.class);
        Board board2 = mock(Board.class);
        when(board1.getId()).thenReturn(1L);
        when(board2.getId()).thenReturn(2L);

        List list = mock(List.class);
        when(list.getBoard()).thenReturn(board1);

        Card card = mock(Card.class);
        when(card.getList()).thenReturn(list);

        Label label = mock(Label.class);
        when(label.getBoard()).thenReturn(board2);

        // when & then
        assertThrows(MismatchedBoardException.class, () -> cardLabelValidator.validateMismatchBoard(card, label));
    }

    @Test
    @DisplayName("Card와 Label이 동일한 Board에 속해 있을 때 예외가 발생하지 않음")
    void validateMismatchBoard_Success() {
        // given
        Board board = mock(Board.class);
        when(board.getId()).thenReturn(1L);

        List list = mock(List.class);
        when(list.getBoard()).thenReturn(board);

        Card card = mock(Card.class);
        when(card.getList()).thenReturn(list);

        Label label = mock(Label.class);
        when(label.getBoard()).thenReturn(board);

        // when & then
        assertDoesNotThrow(() -> cardLabelValidator.validateMismatchBoard(card, label));
    }
}