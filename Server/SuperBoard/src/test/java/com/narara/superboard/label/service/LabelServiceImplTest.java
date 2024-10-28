package com.narara.superboard.label.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.common.application.validator.ColorValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.infrastructure.LabelRepository;
import com.narara.superboard.label.interfaces.dto.LabelCreateRequestDto;
import com.narara.superboard.label.interfaces.dto.LabelUpdateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("라벨 서비스에 대한 단위 테스트")
class LabelServiceImplTest implements MockSuperBoardUnitTests {

    @InjectMocks
    private LabelServiceImpl labelService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private ColorValidator colorValidator;

    @Test
    @DisplayName("존재하지 않는 보드 ID로 라벨 생성 시 NotFoundEntityException 발생")
    void shouldThrowExceptionWhenBoardNotFound() {
        // given
        Long nonExistentBoardId = 1L;
        LabelCreateRequestDto requestDto = new LabelCreateRequestDto("Test Label", 0xFFFFFF00L);

        // Mocking: Board가 존재하지 않도록 설정
        when(boardRepository.findById(nonExistentBoardId)).thenReturn(Optional.empty());

        // then: 예외 발생 확인
        NotFoundEntityException exception = assertThrows(
                NotFoundEntityException.class,
                () -> labelService.createLabel(nonExistentBoardId, requestDto)
        );
        assertEquals("해당하는 보드(이)가 존재하지 않습니다. 보드ID: " + nonExistentBoardId, exception.getMessage());

        verify(boardRepository, times(1)).findById(nonExistentBoardId);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("유효하지 않은 색상으로 인해 예외 발생")
    void shouldThrowExceptionWhenInvalidColor(Long color) {
        // given
        LabelCreateRequestDto requestDto = new LabelCreateRequestDto("Test Label", color);

        // Mocking: colorValidator가 유효하지 않은 색상에 대해 예외를 던지도록 설정
        doThrow(new IllegalArgumentException("Invalid color format")).when(colorValidator).validateLabelColor(requestDto);

        // then
        assertThrows(IllegalArgumentException.class, () -> labelService.createLabel(1L, requestDto));
    }

    @Test
    @DisplayName("성공 테스트: 유효한 보드와 색상으로 라벨 생성 성공")
    void shouldCreateLabelSuccessfullyWhenValidDataIsGiven() {
        // given
        Long boardId = 1L;
        LabelCreateRequestDto requestDto = new LabelCreateRequestDto("Test Label", 0xFFFFFF00L);
        Board board = Board.builder().id(boardId).name("Test Board").build();
        Label expectedLabel = Label.builder().id(1L).name(requestDto.name()).color(requestDto.color()).board(board).build();

        // Mocking: 검증 로직을 모킹
        doNothing().when(colorValidator).validateLabelColor(requestDto);
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(labelRepository.save(any(Label.class))).thenReturn(expectedLabel);

        // when
        Label result = labelService.createLabel(boardId, requestDto);

        // then
        assertNotNull(result);
        assertEquals(expectedLabel.getName(), result.getName());
        assertEquals(expectedLabel.getColor(), result.getColor());
        assertEquals(expectedLabel.getBoard(), result.getBoard());

        verify(colorValidator, times(1)).validateLabelColor(requestDto);
        verify(boardRepository, times(1)).findById(boardId);
        verify(labelRepository, times(1)).save(any(Label.class));
    }

    @Test
    @DisplayName("존재하지 않는 라벨 ID로 조회 시 NotFoundEntityException 발생")
    void shouldThrowExceptionWhenLabelNotFound() {
        // given
        Long nonExistentLabelId = 1L;

        // Mocking: Label이 존재하지 않도록 설정
        when(labelRepository.findById(nonExistentLabelId)).thenReturn(Optional.empty());

        // then: 예외 발생 확인
        NotFoundEntityException exception = assertThrows(
                NotFoundEntityException.class,
                () -> labelService.getLabel(nonExistentLabelId)
        );
        assertEquals("해당하는 라벨(이)가 존재하지 않습니다. 라벨ID: " + nonExistentLabelId, exception.getMessage());

        verify(labelRepository, times(1)).findById(nonExistentLabelId);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0xFFFFFFFF + 1, -1L}) // 잘못된 색상 값
    @DisplayName("유효하지 않은 색상 값으로 업데이트 시 예외 발생")
    void shouldThrowExceptionWhenUpdatingWithInvalidColor(Long invalidColor) {
        // given
        Long labelId = 1L;
        LabelUpdateRequestDto updateRequestDto = new LabelUpdateRequestDto("Updated Label", invalidColor);

        // Mocking: colorValidator가 유효하지 않은 색상에 대해 예외를 던지도록 설정
        doThrow(new IllegalArgumentException("Invalid color format")).when(colorValidator).validateLabelColor(updateRequestDto);

        // then
        assertThrows(IllegalArgumentException.class, () -> labelService.updateLabel(labelId, updateRequestDto));
        verify(colorValidator, times(1)).validateLabelColor(updateRequestDto);
    }

    @Test
    @DisplayName("존재하는 라벨 ID로 조회 시 Label 반환")
    void shouldReturnLabelWhenLabelExists() {
        // given
        Long labelId = 1L;
        Label label = Label.builder()
                .id(labelId)
                .name("Test Label")
                .color(0xFFFFFF00L)
                .build();

        // Mocking: Label이 존재하도록 설정
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));

        // when
        Label result = labelService.getLabel(labelId);

        // then
        assertNotNull(result);
        assertEquals(labelId, result.getId());
        assertEquals("Test Label", result.getName());

        verify(labelRepository, times(1)).findById(labelId);
    }


    @Test
    @DisplayName("유효한 데이터로 라벨 업데이트 성공")
    void shouldUpdateLabelSuccessfullyWhenValidDataIsGiven() {
        // given
        Long labelId = 1L;
        LabelUpdateRequestDto updateRequestDto = new LabelUpdateRequestDto("Updated Label", 0xFFFFFF00L);

        Label existingLabel = Label.builder()
                .id(labelId)
                .name("Old Label")
                .color(0x000000FFL)
                .build();

        // Mocking: 기존 라벨을 찾도록 설정
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(existingLabel));

        // Mocking: colorValidator가 유효한 색상에 대해 예외를 던지지 않도록 설정
        doNothing().when(colorValidator).validateLabelColor(updateRequestDto);

        // when
        Label updatedLabel = labelService.updateLabel(labelId, updateRequestDto);

        // then
        assertEquals("Updated Label", updatedLabel.getName());
        assertEquals(0xFFFFFF00L, updatedLabel.getColor());
        verify(labelRepository, times(1)).findById(labelId);
        verify(colorValidator, times(1)).validateLabelColor(updateRequestDto);
    }

    @Test
    @DisplayName("성공 테스트: 존재하는 라벨 ID로 라벨 삭제")
    void shouldDeleteLabelSuccessfullyWhenLabelExists() {
        // given
        Long labelId = 1L;
        Label label = Label.builder()
                .id(labelId)
                .name("Test Label")
                .color(0xFFFFFF00L)
                .build();

        // Mocking: Label이 존재하도록 설정
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));

        // when & then
        assertDoesNotThrow(() -> labelService.deleteLabel(labelId), "라벨 삭제 시 예외가 발생하면 안 됩니다.");

        // 라벨이 정상적으로 삭제되었는지 확인
        verify(labelRepository, times(1)).findById(labelId);
        verify(labelRepository, times(1)).delete(label);
    }

    @Test
    @DisplayName("보드가 존재하지 않을 때 NotFoundEntityException 발생")
    void shouldThrowExceptionWhenBoardDoesNotExist() {
        // given
        Long nonExistentBoardId = 999L;

        // Mocking: 보드가 존재하지 않도록 설정
        when(boardRepository.findById(nonExistentBoardId)).thenReturn(Optional.empty());

        // then: 예외 발생 확인
        NotFoundEntityException exception = assertThrows(
                NotFoundEntityException.class,
                () -> labelService.getAllLabelsByBoardId(nonExistentBoardId)
        );

        assertEquals("해당하는 보드(이)가 존재하지 않습니다. 보드ID: " + nonExistentBoardId, exception.getMessage());

        verify(boardRepository, times(1)).findById(nonExistentBoardId);
        verify(labelRepository, never()).findAllByBoard(any(Board.class));
    }

    @Test
    @DisplayName("보드에 라벨이 없을 때 빈 리스트 반환")
    void shouldReturnEmptyListWhenNoLabelsForBoard() {
        // given
        Long boardId = 1L;
        Board board = Board.builder().id(boardId).name("Test Board").build();

        // Mocking
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(labelRepository.findAllByBoard(board)).thenReturn(Collections.emptyList());

        // when
        List<Label> labels = labelService.getAllLabelsByBoardId(boardId);

        // then
        assertTrue(labels.isEmpty(), "라벨 리스트가 비어 있어야 합니다.");
        verify(boardRepository, times(1)).findById(boardId);
        verify(labelRepository, times(1)).findAllByBoard(board);
    }

    @Test
    @DisplayName("보드에 하나의 라벨이 있을 때 해당 라벨 반환")
    void shouldReturnSingleLabelWhenOneLabelExistsForBoard() {
        // given
        Long boardId = 1L;
        Board board = Board.builder().id(boardId).name("Test Board").build();
        Label label = Label.builder().id(1L).name("Label 1").color(0xFFFFFFL).board(board).build();

        // Mocking
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(labelRepository.findAllByBoard(board)).thenReturn(List.of(label));

        // when
        List<Label> labels = labelService.getAllLabelsByBoardId(boardId);

        // then
        assertEquals(1, labels.size(), "라벨 리스트 크기는 1이어야 합니다.");
        assertEquals("Label 1", labels.get(0).getName());
        verify(boardRepository, times(1)).findById(boardId);
        verify(labelRepository, times(1)).findAllByBoard(board);
    }

    @Test
    @DisplayName("보드에 두 개의 라벨이 있을 때 두 라벨 반환")
    void shouldReturnTwoLabelsWhenTwoLabelsExistForBoard() {
        // given
        Long boardId = 1L;
        Board board = Board.builder().id(boardId).name("Test Board").build();

        Label label1 = Label.builder().id(1L).name("Label 1").color(0xFFFFFFL).board(board).build();
        Label label2 = Label.builder().id(2L).name("Label 2").color(0x000000L).board(board).build();
        List<Label> labelList = List.of(label1, label2);

        // Mocking
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(labelRepository.findAllByBoard(board)).thenReturn(labelList);

        // when
        List<Label> labels = labelService.getAllLabelsByBoardId(boardId);

        // then
        assertEquals(2, labels.size(), "라벨 리스트 크기는 2여야 합니다.");
        assertEquals("Label 1", labels.get(0).getName());
        assertEquals("Label 2", labels.get(1).getName());
        verify(boardRepository, times(1)).findById(boardId);
        verify(labelRepository, times(1)).findAllByBoard(board);
    }
}