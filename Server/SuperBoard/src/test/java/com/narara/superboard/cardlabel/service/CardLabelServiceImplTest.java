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
    @DisplayName("실패 테스트: CardLabel이 이미 존재할 때 EntityAlreadyExistsException 발생")
    void createCardLabel_CardLabelAlreadyExists() {
        // given
        Card card = Card.builder().id(1L).build();
        Label label = Label.builder().id(1L).build();

        // CardLabel이 이미 존재하는 경우를 모킹
        CardLabel existingCardLabel = CardLabel.createCardLabel(card, label);
        when(cardLabelRepository.findByCardAndLabel(card, label)).thenReturn(Optional.of(existingCardLabel));

        // then
        assertThrows(EntityAlreadyExistsException.class, () -> {
            cardLabelService.createCardLabel(card, label);
        });

        // 검증: CardLabel이 이미 존재하므로 저장(save) 호출이 발생하지 않음
        verify(cardLabelRepository, times(1)).findByCardAndLabel(card, label);
        verify(cardLabelRepository, never()).save(any(CardLabel.class));
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
    @DisplayName("성공 테스트: CardLabel이 존재하지 않을 때 새로 생성")
    void changeCardLabelIsActivated_CardLabelNotExist_CreateNew() {
        // given
        Card card = Card.builder().id(1L).build();
        Label label = Label.builder().id(1L).build();

        CardLabel newCardLabel = CardLabel.createCardLabel(card, label);

        // CardLabel이 존재하지 않음을 모킹
        when(cardLabelRepository.findByCardAndLabel(card, label)).thenReturn(Optional.empty());
        when(cardLabelRepository.save(any(CardLabel.class))).thenReturn(newCardLabel);

        // when
        CardLabel result = cardLabelService.changeCardLabelIsActivated(card, label);

        // then
        assertNotNull(result, "새로 생성된 CardLabel이 반환되어야 합니다.");
        assertEquals(card, result.getCard());
        assertEquals(label, result.getLabel());
        assertTrue(result.getIsActivated(), "새로 생성된 CardLabel의 isActivated는 true 여야 합니다.");

        // 검증: CardLabel 조회 1회, 새로 생성된 CardLabel 저장 1회
        verify(cardLabelRepository, times(1)).findByCardAndLabel(card, label);
        verify(cardLabelRepository, times(1)).save(any(CardLabel.class));
    }

    @Test
    @DisplayName("성공 테스트: CardLabel이 이미 존재할 때 isActivated 상태 변경")
    void changeCardLabelIsActivated_CardLabelExists_ToggleActivation() {
        // given
        Card card = Card.builder().id(1L).build();
        Label label = Label.builder().id(1L).build();

        CardLabel existingCardLabel = spy(CardLabel.createCardLabel(card, label));

        // 초기 상태를 false로 설정해두기 위해 토글 한 번 적용
        existingCardLabel.changeIsActivated(); // 초기 상태 false로 설정

        // CardLabel이 존재하도록 모킹
        when(cardLabelRepository.findByCardAndLabel(card, label)).thenReturn(Optional.of(existingCardLabel));

        // when
        CardLabel result = cardLabelService.changeCardLabelIsActivated(card, label);

        // then
        assertNotNull(result, "기존 CardLabel이 반환되어야 합니다.");
        assertEquals(existingCardLabel, result, "기존 CardLabel이 반환되어야 합니다.");
        assertTrue(result.getIsActivated(), "isActivated 상태가 true로 변경되어야 합니다.");

        // 검증: CardLabel 조회 1회 발생해야 함
        verify(cardLabelRepository, times(1)).findByCardAndLabel(card, label);
    }

}