package com.narara.superboard.board.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.infrastrucuture.BoardRepository;
import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.board.interfaces.dto.BoardDetailResponseDto;
import com.narara.superboard.board.service.validator.BoardValidator;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.exception.NotFoundEntityException;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BoardServiceImplTest {

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardValidator boardValidator;


    @Mock
    private CoverHandler coverHandler;

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


}
