package com.narara.superboard.cardlabel.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.infrastructrue.CardLabelRepository;
import com.narara.superboard.cardlabel.interfaces.dto.CardLabelDto;
import com.narara.superboard.cardlabel.service.validator.CardLabelValidator;
import com.narara.superboard.common.exception.cardlabel.MismatchedBoardException;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.infrastructure.LabelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

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
        when(cardRepository.findByIdAndIsDeletedFalse(cardId)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            Card card = cardRepository.findByIdAndIsDeletedFalse(cardId).orElseThrow(EntityNotFoundException::new);
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

    /**
     * 카드 라벨 조회(전체 라벨+내가 선택한 라벨) TEST ------------------------------------------------------
     */
    @Test
    @DisplayName("카드 라벨 전체 조회 성공 테스트")
    public void testGetCardLabels_SomeLabelsUsedByCard() {
        // Arrange
        Long cardId = 1L;
        Long boardId = 2L;

        Board board = Board.builder()
                .id(boardId)
                .cover(null)
                .name("dd")
                .build();

        // 카드 ID에 해당하는 보드 ID를 조회하는 부분을 모킹합니다.
        when(cardRepository.findBoardByCardId(cardId)).thenReturn(board);

        // 보드에 연결된 라벨 목록을 모킹합니다.
        List<Label> boardLabels = Arrays.asList(
                new Label(1L, board,"Label1", 1L),
                new Label(2L,board, "Label2", 2L),
                new Label(3L,board, "Label3", 3L)
        );

        when(labelRepository.findAllByBoard(board)).thenReturn(boardLabels);

        // 카드에 사용된 라벨 ID 목록을 모킹합니다. 카드가 Label1과 Label3을 사용한다고 가정합니다.
        Set<Long> cardLabelIds = Set.of(1L, 3L);
        when(cardLabelRepository.findLabelIdsByCardId(cardId)).thenReturn(cardLabelIds);

        // Act
        List<CardLabelDto> cardLabels = cardLabelService.getCardLabelCollection(cardId);

        // Assert
        assertEquals(3, cardLabels.size());  // 보드에 있는 라벨이 총 3개이므로, 3개의 DTO가 반환됩니다.

        assertTrue(cardLabels.get(0).IsActivated());  // Label1은 카드에 사용됩니다.
        assertFalse(cardLabels.get(1).IsActivated()); // Label2는 사용되지 않습니다.
        assertTrue(cardLabels.get(2).IsActivated());  // Label3은 카드에 사용됩니다.

        // DTO에 라벨 정보가 정확히 포함되었는지 추가 검증
        assertEquals("Label1", cardLabels.get(0).name(), "Label1의 이름이 정확히 포함되어야 합니다.");
        assertEquals(1L, cardLabels.get(0).labelId(), "Label1의 ID가 정확히 포함되어야 합니다.");
    }

    @Test
    @DisplayName("카드가 보드에 속하지 않는 경우 - 데이터 무결성 예외 발생")
    public void testGetCardLabels_CardNotAssociatedWithBoard() {
        // Arrange
        Long cardId = 1L;

        // 카드 ID로 보드를 찾지 못하도록 모킹합니다.
        when(cardRepository.findBoardByCardId(cardId)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            cardLabelService.getCardLabelCollection(cardId);
        });

        assertEquals("ID가 " + cardId + "인 카드가 보드와 연결되지 않았습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("보드에 라벨이 없는 경우 - 빈 리스트 반환")
    public void testGetCardLabels_NoLabelsOnBoard() {
        // Arrange
        Long cardId = 1L;
        Long boardId = 2L;

        Board board = Board.builder().id(boardId).name("Test Board").build();
        when(cardRepository.findBoardByCardId(cardId)).thenReturn(board);

        // 보드에 연결된 라벨이 없도록 모킹합니다.
        when(labelRepository.findAllByBoard(board)).thenReturn(Collections.emptyList());

        // Act
        List<CardLabelDto> cardLabels = cardLabelService.getCardLabelCollection(cardId);

        // Assert
        assertTrue(cardLabels.isEmpty(), "라벨이 없는 경우 빈 리스트가 반환되어야 합니다.");
    }

    @Test
    @DisplayName("카드가 사용하는 라벨이 없는 경우 - 모든 라벨이 미사용으로 표시")
    public void testGetCardLabels_NoLabelsUsedByCard() {
        // Arrange
        Long cardId = 1L;
        Long boardId = 2L;

        Board board = Board.builder().id(boardId).name("Test Board").build();
        when(cardRepository.findBoardByCardId(cardId)).thenReturn(board);

        List<Label> boardLabels = Arrays.asList(
                new Label(1L, board, "Label1", 1L),
                new Label(2L, board, "Label2", 2L)
        );
        when(labelRepository.findAllByBoard(board)).thenReturn(boardLabels);

        // 카드에 연결된 라벨이 없도록 빈 Set을 모킹합니다.
        when(cardLabelRepository.findLabelIdsByCardId(cardId)).thenReturn(Collections.emptySet());

        // Act
        List<CardLabelDto> cardLabels = cardLabelService.getCardLabelCollection(cardId);

        // Assert
        assertEquals(2, cardLabels.size(), "보드에 있는 라벨의 개수와 일치해야 합니다.");
        assertFalse(cardLabels.get(0).IsActivated(), "Label1은 사용되지 않아야 합니다.");
        assertFalse(cardLabels.get(1).IsActivated(), "Label2도 사용되지 않아야 합니다.");
    }

    @Test
    @DisplayName("유효하지 않은 카드 ID - 데이터 무결성 예외 발생")
    public void testGetCardLabels_InvalidCardId() {
        // Arrange
        Long invalidCardId = 99L;

        // 유효하지 않은 카드 ID로 보드를 찾지 못하도록 모킹합니다.
        when(cardRepository.findBoardByCardId(invalidCardId)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            cardLabelService.getCardLabelCollection(invalidCardId);
        });

        assertEquals("ID가 " + invalidCardId + "인 카드가 보드와 연결되지 않았습니다.", exception.getMessage());
    }


}
