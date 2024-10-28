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
        Long labelId = 1L;

        Card card = Card.builder().id(1L).build();

        // Label이 존재하지 않는 경우를 모킹
        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            Label label = labelRepository.findById(labelId).orElseThrow(EntityNotFoundException::new);
            cardLabelService.createCardLabel(card, label);
        });
        verify(labelRepository, times(1)).findById(labelId);
        verify(cardLabelRepository, never()).save(any(CardLabel.class));
    }

    @Test
    @DisplayName("실패 테스트: Card가 존재하지 않을 때 EntityNotFoundException 발생")
    void createCardLabel_CardNotFound() {
        // given
        Long cardId = 1L;
        Long labelId = 1L;

        // Label 객체는 정상적으로 생성되는 상황으로 모킹
        Label label = Label.builder().id(labelId).build();

        // Card가 존재하지 않는 경우 모킹
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            Card card = cardRepository.findById(cardId).orElseThrow(EntityNotFoundException::new);
            cardLabelService.createCardLabel(card, label);
        });

        // 검증: 카드 저장 로직은 호출되지 않음
        verify(cardLabelRepository, never()).save(any(CardLabel.class));
    }

    @Test
    @DisplayName("실패 테스트: 다른 보드에 속하는 Card와 Label로 인해 MismatchedBoardException 발생")
    void createCardLabel_MismatchedBoard() {
        // given
        Board board1 = Board.builder().id(1L).build();
        Board board2 = Board.builder().id(2L).build();

        Card card = Card.builder()
                .id(1L)
                .list(com.narara.superboard.list.entity.List.builder().board(board1).build())
                .build();

        Label label = Label.builder()
                .id(1L)
                .board(board2)
                .build();

        doThrow(MismatchedBoardException.class).when(cardLabelValidator).validateMismatchBoard(card, label);

        // then
        assertThrows(MismatchedBoardException.class, () -> cardLabelService.createCardLabel(card, label));
        verify(cardLabelValidator, times(1)).validateMismatchBoard(card, label);
    }

    @Test
    @DisplayName("성공 테스트: 동일한 보드에 속하는 Card와 Label로 CardLabel 생성")
    void createCardLabel_Success() {
        // given
        Board board = Board.builder().id(1L).build();

        Card card = Card.builder()
                .id(1L)
                .list(com.narara.superboard.list.entity.List.builder().board(board).build())
                .build();

        Label label = Label.builder()
                .id(1L)
                .board(board)
                .build();

        CardLabel cardLabel = CardLabel.createCardLabel(card, label);

        when(cardLabelRepository.findByCardAndLabel(card, label)).thenReturn(Optional.empty());
        when(cardLabelRepository.save(any(CardLabel.class))).thenReturn(cardLabel);

        // when
        CardLabel result = cardLabelService.createCardLabel(card, label);

        // then
        assertNotNull(result);
        assertEquals(card, result.getCard());
        assertEquals(label, result.getLabel());
        assertTrue(result.getIsActivated());
        verify(cardLabelValidator, times(1)).validateMismatchBoard(card, label);
        verify(cardLabelRepository, times(1)).save(any(CardLabel.class));
    }

    @Test
    @DisplayName("카드와 라벨이 이미 연결되어 있을 때 기존 CardLabel 반환")
    void shouldReturnExistingCardLabelIfExists() {
        // given
        Board board = Board.builder().id(1L).build();

        Card card = Card.builder()
                .id(1L)
                .list(com.narara.superboard.list.entity.List.builder().board(board).build())
                .build();

        Label label = Label.builder()
                .id(1L)
                .board(board)
                .build();

        CardLabel existingCardLabel = CardLabel.createCardLabel(card, label);

        when(cardLabelRepository.findByCardAndLabel(card, label)).thenReturn(Optional.of(existingCardLabel));

        // when
        CardLabel result = cardLabelService.createCardLabel(card, label);

        // then
        assertNotNull(result);
        assertEquals(existingCardLabel, result, "이미 존재하는 CardLabel 객체를 반환해야 합니다.");
        verify(cardLabelValidator, times(1)).validateMismatchBoard(card, label);
        verify(cardLabelRepository, never()).save(any(CardLabel.class));
    }

}