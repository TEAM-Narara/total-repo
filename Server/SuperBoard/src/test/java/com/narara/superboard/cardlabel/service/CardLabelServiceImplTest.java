package com.narara.superboard.cardlabel.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.infrastructrue.CardLabelRepository;
import com.narara.superboard.cardlabel.service.validator.CardLabelValidator;
import com.narara.superboard.common.exception.cardlabel.MismatchedBoardException;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.infrastructure.LabelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("카드의 라벨 서비스에 대한 단위 테스트")
class CardLabelServiceImplTest implements MockSuperBoardUnitTests {

    @InjectMocks
    private CardLabelServiceImpl cardLabelService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private CardLabelRepository cardLabelRepository;

    @Mock
    private CardLabelValidator cardLabelValidator;

    @Test
    @DisplayName("실패 테스트: Label이 존재하지 않을 때 EntityNotFoundException 발생")
    void createCardLabel_LabelNotFound() {
        // given
        Long cardId = 1L;
        Long labelId = 1L;

        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> cardLabelService.createCardLabel(cardId, labelId));
        verify(labelRepository, times(1)).findById(labelId);
        verify(cardRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("실패 테스트: Card가 존재하지 않을 때 IllegalArgumentException 발생")
    void createCardLabel_CardNotFound() {
        // given
        Long cardId = 1L;
        Long labelId = 1L;

        Label label = Label.builder().id(labelId).build();
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // then
        assertThrows(IllegalArgumentException.class, () -> cardLabelService.createCardLabel(cardId, labelId));
        verify(labelRepository, times(1)).findById(labelId);
        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    @DisplayName("실패 테스트: 다른 보드에 속하는 Card와 Label로 인해 MismatchedBoardException 발생")
    void createCardLabel_MismatchedBoard() {
        // given
        Long cardId = 1L;
        Long labelId = 1L;

        Board board1 = Board.builder().id(1L).build();
        Board board2 = Board.builder().id(2L).build();

        Card card = Card.builder()
                .id(cardId)
                .list(com.narara.superboard.list.entity.List.builder().board(board1).build())
                .build();

        Label label = Label.builder()
                .id(labelId)
                .board(board2)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));

        // Mock the validator to throw MismatchedBoardException
        doThrow(MismatchedBoardException.class).when(cardLabelValidator).validateMismatchBoard(card, label);

        // then
        assertThrows(MismatchedBoardException.class, () -> cardLabelService.createCardLabel(cardId, labelId));
        verify(cardLabelValidator, times(1)).validateMismatchBoard(card, label);
    }

    @Test
    @DisplayName("성공 테스트: 동일한 보드에 속하는 Card와 Label로 CardLabel 생성")
    void createCardLabel_Success() {
        // given
        Long cardId = 1L;
        Long labelId = 1L;

        Board board = Board.builder().id(1L).build();

        Card card = Card.builder()
                .id(cardId)
                .list(com.narara.superboard.list.entity.List.builder().board(board).build())
                .build();

        Label label = Label.builder()
                .id(labelId)
                .board(board)
                .build();

        CardLabel cardLabel = CardLabel.builder().card(card).label(label).build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));
        when(cardLabelRepository.findByCardAndLabel(card, label)).thenReturn(Optional.empty());
        when(cardLabelRepository.save(any(CardLabel.class))).thenReturn(cardLabel);

        // when
        CardLabel result = cardLabelService.createCardLabel(cardId, labelId);

        // then
        assertNotNull(result);
        assertEquals(card, result.getCard());
        assertEquals(label, result.getLabel());
        verify(cardLabelValidator, times(1)).validateMismatchBoard(card, label);
        verify(cardLabelRepository, times(1)).save(any(CardLabel.class));
    }

}