package com.narara.superboard.board.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.exception.BoardInvalidVisibilityFormatException;
import com.narara.superboard.board.infrastrucuture.BoardRepository;
import com.narara.superboard.board.interfaces.dto.*;
import com.narara.superboard.board.service.validator.BoardValidator;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import com.narara.superboard.common.exception.cover.NotFoundCoverValueException;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BoardServiceImplTest {

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardValidator boardValidator;

    @Mock
    private CoverValidator coverValidator;

    @Mock
    private CoverHandler coverHandler;

    /**
     * 가상의 객체
     * 주로 단위 테스트에서 의존성을 격리하고 특정 메서드의 동작을 시뮬레이션하는 데 사용됩니다.
     * 실제 로직 실행을 방지, 메서드 호출 검증, 메서드 동작을 제어
     *
     * new Board()는
     * 실제 로직을 테스트할 때 사용되는 실제 객체 생성 방법
     * 메서드 호출 검증 불가능, 메서드 동작을 제어 불가능
     */
    @Mock
    private Board board;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("보드와 관련된 보드에 대한 조회 성공 테스트")
    void testGetBoardCollectionResponseDtoSuccess() {
        // given
        Long boardId = 1L;

        // cover 정보를 담는 Map 객체 생성
        Map<String, Object> cover1 = new HashMap<>();
        cover1.put("type", "COLOR");
        cover1.put("value", "#ffffff");

        Map<String, Object> cover2 = new HashMap<>();
        cover2.put("type", "IMAGE");
        cover2.put("value", "https://example.com/image.jpg");

        // Board 객체 생성
        Board mockBoard1 = new Board(1L, "보드 1", cover1);
        Board mockBoard2 = new Board(2L, "보드 2", cover2);

        List<Board> mockBoardList = Arrays.asList(mockBoard1, mockBoard2);

        // boardRepository의 findAllByBoardId 호출 시 mock 데이터를 반환
        when(boardRepository.findAllByWorkSpaceId(boardId)).thenReturn(mockBoardList);

        // CoverHandler 모킹
        when(coverHandler.getTypeValue(cover1)).thenReturn("COLOR");
        when(coverHandler.getTypeValue(cover2)).thenReturn("IMAGE");
        when(coverHandler.getValue(cover1)).thenReturn("#ffffff");
        when(coverHandler.getValue(cover2)).thenReturn("https://example.com/image.jpg");

        // when
        BoardCollectionResponseDto result = boardService.getBoardCollectionResponseDto(boardId);

        // then
        assertEquals(2, result.boardDetailResponseDtoList().size());

        BoardDetailResponseDto board1 = result.boardDetailResponseDtoList().get(0);
        assertEquals(1L, board1.id());
        assertEquals("보드 1", board1.name());
        assertEquals("COLOR", board1.backgroundType());
        assertEquals("#ffffff", board1.backgroundValue());

        BoardDetailResponseDto board2 = result.boardDetailResponseDtoList().get(1);
        assertEquals(2L, board2.id());
        assertEquals("보드 2", board2.name());
        assertEquals("IMAGE", board2.backgroundType());
        assertEquals("https://example.com/image.jpg", board2.backgroundValue());
    }

    @Test
    @DisplayName("보드 생성 성공 테스트")
    void testCreateBoardSuccess() {
        // given
        Long boardId = 1L;
        Map<String, Object> background = new HashMap<>();
        background.put("type", "COLOR");
        background.put("value", "#ffffff");

        BoardCreateRequestDto boardCreateRequestDto = new BoardCreateRequestDto(
                "보드 이름",
                "PRIVATE",
                background
        );

        Board savedBoard = Board.builder()
                .id(boardId)
                .cover(background)
                .name("보드 이름")
                .visibility(Visibility.PRIVATE)
                .build();

        // Mocking: 검증 로직을 모킹
        doNothing().when(boardValidator).validateNameIsPresent(boardCreateRequestDto);
        doNothing().when(boardValidator).validateVisibilityIsValid(boardCreateRequestDto);
        doNothing().when(boardValidator).validateVisibilityIsPresent(boardCreateRequestDto);

        // Mocking: boardRepository.save 호출 시 저장된 board 객체 반환
        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);

        // when
        Long savedBoardId = boardService.createBoard(boardCreateRequestDto);

        // then
        assertEquals(boardId, savedBoardId);
        verify(boardValidator, times(1)).validateNameIsPresent(boardCreateRequestDto);
        verify(boardValidator, times(1)).validateVisibilityIsValid(boardCreateRequestDto);
        verify(boardValidator, times(1)).validateVisibilityIsPresent(boardCreateRequestDto);
        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    @DisplayName("background가 null인 경우에도 보드 생성 성공")
    void createBoard_WhenBackgroundIsNull_ThenSuccess() {
        // given
        String name = "테스트 보드";
        Map<String, Object> background = null;
        String visibility = "PUBLIC";

        BoardCreateRequestDto requestDto = new BoardCreateRequestDto(name, visibility, background);

        Board expectedBoard = Board.builder()
                .cover(background)
                .name(name)
                .visibility(Visibility.PUBLIC)
                .id(1L)
                .build();

        // when
        when(boardRepository.save(any(Board.class))).thenReturn(expectedBoard);

        // then
        Long boardId = boardService.createBoard(requestDto);

        assertEquals(1L, boardId);
        verify(boardValidator).validateNameIsPresent(requestDto);
        verify(boardValidator).validateVisibilityIsValid(requestDto);
        verify(boardValidator).validateVisibilityIsPresent(requestDto);
        verify(boardRepository).save(any(Board.class));
    }

    @DisplayName("보드를 찾을 수 없는 경우 예외 발생")
    @Test
    void testGetBoard_NotFound() {
        // given
        Long boardId = 1L;
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());  // 빈 Optional을 반환하도록 설정

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class,
                () -> boardService.getBoard(boardId));  // 예외가 발생하는지 확인

        // 추가 검증: 예외 객체에 담긴 ID와 엔티티 타입이 정확한지 확인
        assertEquals(boardId, exception.getId());  // 예외에 저장된 ID가 일치하는지 확인
        assertEquals("Board", exception.getEntity());  // 예외에 저장된 엔티티 타입이 일치하는지 확인

        verify(boardRepository, times(1)).findById(boardId);  // findById가 한 번 호출되었는지 확인
    }


    // Board 객체를 실제로 만들어서 테스트하기 위한 데이터 제공 메서드
    static Stream<Board> provideBoards() {
        return Stream.of(
                new Board(1L, "Board 1"),
                new Board(2L, "Board 2"),
                new Board(3L, "Board 3")
        );
    }

    @DisplayName("보드를 성공적으로 찾을 수 있는 경우")
    @ParameterizedTest
    @MethodSource("provideBoards")
    void testGetBoard_Success(Board board) {
        // given
        Long boardId = board.getId();  // Board의 ID
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));  // 정상적으로 Board가 반환되는 상황 설정

        // when
        Board result = boardService.getBoard(boardId);  // 실제 getBoard 호출

        // then
        assertNotNull(result);  // 결과가 null이 아닌지 확인
        assertEquals(board, result);  // 반환된 객체가 기대한 객체와 일치하는지 확인
        verify(boardRepository, times(1)).findById(boardId);  // findById가 한 번 호출되었는지 검증
    }

    @DisplayName("보드 삭제 성공 테스트")
    @Test
    void deleteBoard_Success() {
        // Given
        Long boardId = 1L;

        // 가정: 이 ID에 대한 워크스페이스가 존재함
        Board mockBoard = Board.builder()
                .id(boardId)
                .name("my Board")
                .build();

        // getBoard 메서드가 워크스페이스를 반환하도록 설정
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(mockBoard));

        // When
        boardService.deleteBoard(boardId);  // deleteBoard 메서드 호출

        // Then
        verify(boardRepository, times(1)).findById(boardId);
        verify(boardRepository, times(1)).delete(mockBoard);
    }

    @ParameterizedTest
    @DisplayName("[ADMIN] 보드 수정 시 이름이 존재하지 않으면 NotFoundException 발생 테스트")
    @CsvSource({
            "'', '{\"type\":\"COLOR\",\"value\":\"#ffffff\"}', 'WORKSPACE'",  // 이름이 빈 값인 경우
            "null, '{\"type\":\"COLOR\",\"value\":\"#ffffff\"}', 'WORKSPACE'"  // 이름이 null인 경우
    })
    void testUpdateBoard_NameInvalidValue(String name, String backgroundJson, String visibility) {
        // given
        Long boardId = 1L;
        Map<String, Object> background = backgroundJson.isEmpty() ? null : Map.of("type", "COLOR", "value", "#ffffff");

        // 요청 DTO 생성
        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto(name, background, visibility);

        // Mocking: validateNameIsPresent 호출 시 예외 발생 설정
        doThrow(new NotFoundException("Board", "name")).when(boardValidator).validateNameIsPresent(requestDto);

        // when & then: 이름 값이 없을 때 예외가 발생하는지 확인
        assertThrows(NotFoundException.class, () -> boardService.updateBoard(boardId, requestDto));

        // 검증: 이름 검증이 호출되었는지 확인
        verify(boardValidator, times(1)).validateNameIsPresent(requestDto);
        verify(boardValidator, times(0)).validateVisibilityIsPresent(requestDto); // 호출되지 않음
    }

    @ParameterizedTest
    @DisplayName("[ADMIN] 보드 수정 시 가시성이 존재하지 않으면 NotFoundException 발생 테스트")
    @CsvSource({
            "'Board Name', '{\"type\":\"COLOR\",\"value\":\"#ffffff\"}', ''",  // 가시성이 빈 값인 경우
            "'Board Name', '{\"type\":\"COLOR\",\"value\":\"#ffffff\"}', 'null'"  // 가시성이 null인 경우
    })
    void testUpdateBoard_VisibilityEmptyValue(String name, String backgroundJson, String visibility) {
        // given
        Long boardId = 1L;
        Map<String, Object> background = backgroundJson.isEmpty() ? null : Map.of("type", "COLOR", "value", "#ffffff");

        // 요청 DTO 생성
        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto(name, background, visibility);

        // Mocking: validateVisibilityIsPresent 호출 시 예외 발생 설정
        doThrow(new NotFoundException("Board", "visibility")).when(boardValidator).validateVisibilityIsPresent(requestDto);

        // when & then: 가시성 값이 없을 때 예외가 발생하는지 확인
        assertThrows(NotFoundException.class, () -> boardService.updateBoard(boardId, requestDto));

        // 검증: 가시성 검증이 호출되었는지 확인
        verify(boardValidator, times(1)).validateNameIsPresent(requestDto); // 호출된 후 호출됨.
        verify(boardValidator, times(1)).validateVisibilityIsPresent(requestDto);
    }

    @ParameterizedTest
    @DisplayName("[ADMIN] 보드 수정 시 가시성이 적절하지 않으면 NotFoundException 발생 테스트")
    @CsvSource({
            "'Board Name', '{\"type\":\"COLOR\",\"value\":\"#ffffff\"}', 'INVALID'",  // 가시성이 빈 값인 경우
            "'Board Name', '{\"type\":\"COLOR\",\"value\":\"#ffffff\"}', 'NULL'"  // 가시성이 null인 경우
    })
    void testUpdateBoard_VisibilityInvalidValue(String name, String backgroundJson, String visibility) {
        // given
        Long boardId = 1L;
        Map<String, Object> background = backgroundJson.isEmpty() ? null : Map.of("type", "COLOR", "value", "#ffffff");

        // 요청 DTO 생성
        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto(name, background, visibility);

        // Mocking: validateVisibilityIsPresent 호출 시 예외 발생 설정
        doThrow(new BoardInvalidVisibilityFormatException()).when(boardValidator).validateVisibilityIsValid(requestDto);

        // when & then: 가시성 값이 없을 때 예외가 발생하는지 확인
        assertThrows(BoardInvalidVisibilityFormatException.class, () -> boardService.updateBoard(boardId, requestDto));

        // 검증: 가시성 검증이 호출되었는지 확인
        verify(boardValidator, times(1)).validateNameIsPresent(requestDto); // 호출된 후 호출됨.
        verify(boardValidator, times(1)).validateVisibilityIsPresent(requestDto);
        verify(boardValidator, times(1)).validateVisibilityIsValid(requestDto);
    }


    @Test
    @DisplayName("보드 수정 시 커버가 존재하는데, 커버에 type 필드가 없으면 NotFoundCoverTypeException 발생")
    void testUpdateBoard_MissingCoverType() {
        // given
        Long boardId = 1L;
        Map<String, Object> coverWithoutType = new HashMap<>();
        coverWithoutType.put("value", "#ffffff");  // type 필드 없음

        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto("보드 이름", coverWithoutType, "PRIVATE");

        // Mock: validateCoverTypeIsEmpty에서 커버에 type 필드가 없으면 예외 발생
        doThrow(new NotFoundCoverTypeException()).when(coverValidator).validateContainCover(requestDto);

        // when & then: 예외가 발생하는지 확인
        assertThrows(NotFoundCoverTypeException.class, () -> boardService.updateBoard(boardId, requestDto));

        // verify: coverValidator가 호출되었는지 확인
        verify(coverValidator, times(1)).validateContainCover(requestDto);
    }

    @Test
    @DisplayName("보드 수정 시 커버가 존재하는데, 커버에 value 필드가 없으면 NotFoundCoverValueException 발생")
    void testUpdateBoard_MissingCoverValue() {
        // given
        Long boardId = 1L;
        Map<String, Object> coverWithoutValue = new HashMap<>();
        coverWithoutValue.put("type", "COLOR");  // value 필드 없음

        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto("보드 이름", coverWithoutValue, "PRIVATE");

        // Mock: validateCoverValueIsEmpty에서 커버에 value 필드가 없으면 예외 발생
        doThrow(new NotFoundCoverValueException()).when(coverValidator).validateContainCover(requestDto);

        // when & then: 예외가 발생하는지 확인
        assertThrows(NotFoundCoverValueException.class, () -> boardService.updateBoard(boardId, requestDto));

        // verify: coverValidator가 호출되었는지 확인
        verify(coverValidator, times(1)).validateContainCover(requestDto);
    }


    @ParameterizedTest
    @DisplayName("보드 수정 성공 테스트")
    @CsvSource({
            "'Board Name', '{\"type\":\"COLOR\",\"value\":\"#ffffff\"}', 'WORKSPACE'",   // 정상 케이스
            "'Another Board Name', '{\"type\":\"IMAGE\",\"value\":\"https://example.com/image.jpg\"}', 'PRIVATE'",  // 이미지 커버 케이스
            "'Valid Board Name', '', 'WORKSPACE'",   // 커버가 null인 경우
            "'Board with Empty Cover', '{\"type\":\"COLOR\"}', 'WORKSPACE'",   // 커버에 값이 빠져있는 경우
            "'Final Test Board', '{\"type\":\"IMAGE\",\"value\":\"https://example.com/final.jpg\"}', 'PRIVATE'"  // 다른 가시성 및 이미지 커버
    })
    void testUpdateBoard_Success(String name, String coverJson, String visibility) {
        // given
        Long boardId = 1L;
        Map<String, Object> cover = coverJson.isEmpty() ? null : Map.of("type", "COLOR", "value", "#ffffff");

        // BoardUpdateRequestDto 생성
        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto(name, cover, visibility);

        // 보드 데이터 모킹 설정 (모의 객체 반환하게 설정)
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(board.updateBoardByAdmin(any(BoardUpdateRequestDto.class))).thenReturn(board);

        // Validator 메서드 호출 시 아무 일도 하지 않도록 설정 (성공 시나리오)
        doNothing().when(boardValidator).validateNameIsPresent(requestDto);
        doNothing().when(boardValidator).validateVisibilityIsPresent(requestDto);
        doNothing().when(boardValidator).validateVisibilityIsValid(requestDto);

        // 커버가 있을 때만 검증하도록 설정
        if (cover != null) {
            doNothing().when(coverValidator).validateContainCover(requestDto);
        }

        // when
        Board updatedBoard = boardService.updateBoard(boardId, requestDto);

        // then
        assertEquals(board, updatedBoard);  // 업데이트된 보드가 원래 보드와 동일해야 함
        verify(boardValidator, times(1)).validateNameIsPresent(requestDto);
        verify(boardValidator, times(1)).validateVisibilityIsPresent(requestDto);
        verify(boardValidator, times(1)).validateVisibilityIsValid(requestDto);

        // 커버가 존재하는 경우에만 커버 검증 호출 확인
        if (cover != null) {
            verify(coverValidator, times(1)).validateContainCover(requestDto);
        } else {
            verify(coverValidator, never()).validateContainCover(requestDto);
        }

        // 보드 수정 로직이 호출되었는지 확인
        verify(board, times(1)).updateBoardByAdmin(requestDto);
    }

    @Test
    @DisplayName("보드 수정 시 커버가 존재하는데, 커버에 type 필드가 없으면 NotFoundCoverTypeException 발생 (by Member)")
    void testUpdateBoardByMember_MissingCoverType() {
        // given
        Long boardId = 1L;
        Map<String, Object> coverWithoutType = new HashMap<>();
        coverWithoutType.put("value", "#ffffff");  // type 필드 없음

        BoardUpdateByMemberRequestDto requestDto = new BoardUpdateByMemberRequestDto("보드 이름", coverWithoutType);

        // Mock: validateCoverTypeIsEmpty에서 커버에 type 필드가 없으면 예외 발생
        doThrow(new NotFoundCoverTypeException()).when(coverValidator).validateContainCover(requestDto);

        // when & then: 예외가 발생하는지 확인
        assertThrows(NotFoundCoverTypeException.class, () -> boardService.updateBoardByMember(boardId, requestDto));

        // verify: coverValidator가 호출되었는지 확인
        verify(coverValidator, times(1)).validateContainCover(requestDto);
    }

    @Test
    @DisplayName("보드 수정 시 커버가 존재하는데, 커버에 value 필드가 없으면 NotFoundCoverValueException 발생 (by Member)")
    void testUpdateBoardByMember_MissingCoverValue() {
        // given
        Long boardId = 1L;
        Map<String, Object> coverWithoutValue = new HashMap<>();
        coverWithoutValue.put("type", "COLOR");  // value 필드 없음

        BoardUpdateByMemberRequestDto requestDto = new BoardUpdateByMemberRequestDto("보드 이름", coverWithoutValue);

        // Mock: validateCoverValueIsEmpty에서 커버에 value 필드가 없으면 예외 발생
        doThrow(new NotFoundCoverValueException()).when(coverValidator).validateContainCover(requestDto);

        // when & then: 예외가 발생하는지 확인
        assertThrows(NotFoundCoverValueException.class, () -> boardService.updateBoardByMember(boardId, requestDto));

        // verify: coverValidator가 호출되었는지 확인
        verify(coverValidator, times(1)).validateContainCover(requestDto);
    }
}
