package com.narara.superboard.board.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.document.BoardHistory;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.exception.BoardInvalidVisibilityFormatException;
import com.narara.superboard.board.exception.BoardNameNotFoundException;
import com.narara.superboard.board.exception.BoardNotFoundException;
import com.narara.superboard.board.infrastructure.BoardHistoryRepository;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.board.interfaces.dto.*;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.board.service.validator.BoardValidator;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.infrastructure.BoardMemberRepository;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.cover.NotFoundCoverTypeException;
import com.narara.superboard.common.exception.cover.NotFoundCoverValueException;

import com.narara.superboard.common.interfaces.dto.CoverDto;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import java.util.Optional;

import java.util.*;
import java.util.stream.Stream;

import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspace.service.kafka.WorkspaceOffsetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import javax.swing.text.html.Option;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("보드 서비스에 대한 단위 테스트")
class BoardServiceImplTest implements MockSuperBoardUnitTests {

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private WorkSpaceRepository workspaceRepository;

    @Mock
    private BoardMemberRepository boardMemberRepository;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private BoardValidator boardValidator;

    @Mock
    private CoverValidator coverValidator;

    @Mock
    private NameValidator nameValidator;


    @Mock
    private CoverHandler coverHandler;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardHistoryRepository boardHistoryRepository;

    @Mock
    private WorkspaceOffsetService workspaceOffsetService;

    @Mock
    private BoardOffsetService boardOffsetService;

    /**
     * 가상의 객체 주로 단위 테스트에서 의존성을 격리하고 특정 메서드의 동작을 시뮬레이션하는 데 사용됩니다. 실제 로직 실행을 방지, 메서드 호출 검증, 메서드 동작을 제어
     * <p>
     * new Board()는 실제 로직을 테스트할 때 사용되는 실제 객체 생성 방법 메서드 호출 검증 불가능, 메서드 동작을 제어 불가능
     */
    @Mock
    private BoardMember boardMember;

    @Mock
    private Board board;

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
        List<BoardDetailResponseDto> result = boardService.getBoardCollectionResponseDto(boardId);

        // then
        assertEquals(2, result.size());

        BoardDetailResponseDto board1 = result.get(0);
        assertEquals(1L, board1.id());
        assertEquals("보드 1", board1.name());
        assertEquals("COLOR", board1.cover().type());
        assertEquals("#ffffff", board1.cover().value());

        BoardDetailResponseDto board2 = result.get(1);
        assertEquals(2L, board2.id());
        assertEquals("보드 2", board2.name());
        assertEquals("IMAGE", board2.cover().type());
        assertEquals("https://example.com/image.jpg", board2.cover().value());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @DisplayName("워크스페이스 ID가 존재하지 않을 때 예외 발생")
    void shouldThrowNotFoundEntityExceptionWhenWorkspaceIdNotFound(Long workspaceId) {
        // given
        BoardCreateRequestDto requestDto = new BoardCreateRequestDto(workspaceId, "Test Board", "PUBLIC", null, false);
        Member member = new Member(1L, "시현", "sisi@naver.com");
        // Mocking workspaceRepository to return empty Optional
        when(workspaceRepository.findByIdAndIsDeletedFalse(workspaceId)).thenReturn(Optional.empty());
        when(memberRepository.findByIdAndIsDeletedFalse(member.getId())).thenReturn(Optional.of(member));

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> {
            boardService.createBoard(member.getId(), requestDto);
        });

        // assertEquals("해당하는 워크스페이스(이)가 존재하지 않습니다. 워크스페이스ID: " + workspaceId, exception.getMessage());
        verify(workspaceRepository, times(1)).findByIdAndIsDeletedFalse(workspaceId);
    }


    static Stream<Arguments> provideBoardCreateRequestData() {
        return Stream.of(
                Arguments.of(1L, "보드 이름1", "COLOR", "#ffffff", "PRIVATE"),
                Arguments.of(2L, "보드 이름2", "GRADIENT", "#123456", "WORKSPACE")
        );
    }

    @ParameterizedTest
    @MethodSource("provideBoardCreateRequestData")
    @DisplayName("워크스페이스 ID가 존재할 때 보드 생성 성공 테스트")
    void testCreateBoardWhenWorkspaceIdExists(Long workspaceId, String name, String backgroundType,
                                              String backgroundValue, String visibility) {
        // given
        Map<String, Object> background = new HashMap<>();
        background.put("type", backgroundType);
        background.put("value", backgroundValue);

        BoardCreateRequestDto requestDto = new BoardCreateRequestDto(
                workspaceId,
                name,
                visibility,
                new CoverDto(backgroundType, backgroundValue),
                false
        );

        Member member = new Member(1L, "시현", "sisi@naver.com");

        WorkSpace workspace = new WorkSpace();
        Board savedBoard = Board.builder()
                .id(workspaceId)
                .cover(background)
                .name(name)
                .visibility(Visibility.fromString(visibility))
                .workSpace(workspace)
                .build();

        // Mocking: 검증 로직을 모킹
        doNothing().when(boardValidator).validateNameIsPresent(requestDto);
        doNothing().when(boardValidator).validateVisibilityIsValid(requestDto);
//        doNothing().when(boardValidator).validateVisibilityIsPresent(requestDto);

        // Mocking: workspaceRepository와 boardRepository의 반환값 설정
        when(workspaceRepository.findByIdAndIsDeletedFalse(workspaceId)).thenReturn(Optional.of(workspace));
        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);
        when(memberRepository.findByIdAndIsDeletedFalse(member.getId())).thenReturn(Optional.of(member));

        // when
        Board board = boardService.createBoard(member.getId(), requestDto);

        // then
        assertEquals(workspaceId, board.getId());
        verify(workspaceRepository, times(1)).findByIdAndIsDeletedFalse(workspaceId);
        verify(boardRepository, times(1)).save(any(Board.class));
        verify(boardValidator, times(1)).validateNameIsPresent(requestDto);
        verify(boardValidator, times(1)).validateVisibilityIsValid(requestDto);
//        verify(boardValidator, times(1)).validateVisibilityIsPresent(requestDto);
        verify(boardMemberRepository, times(1)).save(any(BoardMember.class));
    }

    @Test
    @DisplayName("background가 null인 경우에도 보드 생성 성공")
    void createBoard_WhenBackgroundIsNull_ThenSuccess() {
        // given
        Long workspaceId = 1L;
        String name = "테스트 보드";
        Map<String, Object> background = null;
        String visibility = "WORKSPACE";

        BoardCreateRequestDto requestDto = new BoardCreateRequestDto(workspaceId, name, visibility, null, false);

        WorkSpace workSpace = WorkSpace.builder()
                .id(workspaceId)
                .build();

        when(workspaceRepository.findByIdAndIsDeletedFalse(workspaceId)).thenReturn(Optional.ofNullable(workSpace));

        Board expectedBoard = Board.builder()
                .cover(background)
                .name(name)
                .visibility(Visibility.WORKSPACE)
                .id(1L)
                .workSpace(workSpace)
                .build();

        Member member = new Member(1L, "시현", "sisi@naver.com");

        // when
        when(boardRepository.save(any(Board.class))).thenReturn(expectedBoard);
        when(memberRepository.findByIdAndIsDeletedFalse(member.getId())).thenReturn(Optional.of(member));

        // then
        Board board1 = boardService.createBoard(member.getId(), requestDto);

        assertEquals(1L, board1.getId());
        verify(boardValidator).validateNameIsPresent(requestDto);
        verify(boardValidator).validateVisibilityIsValid(requestDto);
//        verify(boardValidator).validateVisibilityIsPresent(requestDto);
        verify(boardRepository).save(any(Board.class));
        verify(boardMemberRepository).save(any(BoardMember.class));
    }

    @DisplayName("보드를 찾을 수 없는 경우 예외 발생")
    @Test
    void testGetBoard_NotFound() {
        // given
        Long boardId = 1L;
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.empty());  // 빈 Optional을 반환하도록 설정

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class,
                () -> boardService.getBoard(boardId));  // 예외가 발생하는지 확인

        // 추가 검증: 예외 객체에 담긴 ID와 엔티티 타입이 정확한지 확인
        assertEquals(boardId, exception.getId());  // 예외에 저장된 ID가 일치하는지 확인
        assertEquals("Board", exception.getEntity());  // 예외에 저장된 엔티티 타입이 일치하는지 확인

        verify(boardRepository, times(1)).findByIdAndIsDeletedFalse(boardId);  // findById가 한 번 호출되었는지 확인
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
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(board));  // 정상적으로 Board가 반환되는 상황 설정

        // when
        Board result = boardService.getBoard(boardId);  // 실제 getBoard 호출

        // then
        assertNotNull(result);  // 결과가 null이 아닌지 확인
        assertEquals(board, result);  // 반환된 객체가 기대한 객체와 일치하는지 확인
        verify(boardRepository, times(1)).findByIdAndIsDeletedFalse(boardId);  // findById가 한 번 호출되었는지 검증
    }

    @DisplayName("보드 삭제 성공 테스트")
    @Test
    void deleteBoard_Success() {
        // Given
        Long boardId = 1L;
        Member member = new Member(1L , "시현", "sisi@naver.com");

        // 가정: 이 ID에 대한 워크스페이스가 존재함
        Board mockBoard = Board.builder()
                .id(boardId)
                .name("my Board")
                .workSpace(new WorkSpace(1L, "asdf", 0L))
                .build();

        // getBoard 메서드가 워크스페이스를 반환하도록 설정
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(mockBoard));

        // When
        boardService.deleteBoard(member, boardId);  // deleteBoard 메서드 호출

        // Then
        verify(boardRepository, times(1)).findByIdAndIsDeletedFalse(boardId);
        Assertions.assertTrue(mockBoard.getIsDeleted());
    }

    @ParameterizedTest
    @DisplayName("[ADMIN] 보드 수정 시 가시성이 존재하지 않아도 괜찮아 테스트")
    @CsvSource({
            "'Board Name', '{\"type\":\"COLOR\",\"value\":\"#ffffff\"}'"  // 가시성이 null인 경우
    })
    void testUpdateBoard_VisibilityEmptyValue(String name, String backgroundJson) {
        String visibility = null;

        // given
        Long boardId = 1L;
        Board board = Board.createBoard(
                new BoardCreateRequestDto(1L, "workspace", "WORKSPACE", null, false),
                new WorkSpace(1L, "asdf", 1L)
        );
        Map<String, Object> background = backgroundJson.isEmpty() ? null : Map.of("type", "COLOR", "value", "#ffffff");

        // 요청 DTO 생성
        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto(name,
                new CoverDto((String) background.get("type"), (String) background.get("value")), visibility);

        Member member = new Member(1L, "asdf", "asdf");
        BoardMember boardMember1 = new BoardMember(
                member, Authority.ADMIN
        );

//        when(boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, boardMember.getId()))
//                .thenReturn(Optional.of(boardMember1));

        // when & then: 가시성 값이 없을 때 예외가 발생하는지 확인
        Long memberId = 1L;
        when(boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, memberId)).thenReturn(
                Optional.of(boardMember1)
        );
        when(boardRepository.findByIdAndIsDeletedFalse(boardId))
                .thenReturn(Optional.of(board));
        when(memberRepository.findByIdAndIsDeletedFalse(memberId))
                .thenReturn(Optional.of(member));
        assertDoesNotThrow(() -> {
            Board updatedBoard = boardService.updateBoard(memberId, boardId, requestDto);

            assertEquals(updatedBoard.getVisibility().name(), "WORKSPACE");
        });

        // 검증: 가시성 검증이 호출되었는지 확인
        verify(boardValidator, times(1)).validateBackgroundIsValid(requestDto);
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
        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto(name,
                new CoverDto((String) background.get("type"), (String) background.get("value")), visibility);

        // Mocking: validateVisibilityIsPresent 호출 시 예외 발생 설정
        doThrow(new BoardInvalidVisibilityFormatException()).when(boardValidator).validateVisibilityIsValid(requestDto);

        // when & then: 가시성 값이 없을 때 예외가 발생하는지 확인
        Long memberId = 1L;
//        when(boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, memberId)).thenReturn(Optional.of(new BoardMember(1L, null, null, Authority.ADMIN, true)));
        assertThrows(BoardInvalidVisibilityFormatException.class,
                () -> boardService.updateBoard(memberId, boardId, requestDto));

        // 검증: 가시성 검증이 호출되었는지 확인
//        verify(boardValidator, times(1)).validateVisibilityIsPresent(requestDto);
        verify(boardValidator, times(1)).validateVisibilityIsValid(requestDto);
    }


    @Test
    @DisplayName("보드 수정 시 커버가 존재하는데, 커버에 type 필드가 없으면 NotFoundCoverTypeException 발생")
    void testUpdateBoard_MissingCoverType() {
        // given
        Long boardId = 1L;
        Long memberId = 1L;
        Map<String, Object> coverWithoutType = new HashMap<>();
        coverWithoutType.put("value", "#ffffff");  // type 필드 없음
        Member member = new Member(1L, "시현", "sisi@naver.com");

        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto("보드 이름",
                new CoverDto(" ", "https://"), "PRIVATE");

        // Mock: validateCoverTypeIsEmpty에서 커버에 type 필드가 없으면 예외 발생
        doThrow(new NotFoundCoverTypeException()).when(coverValidator).validateCoverTypeIsValid(requestDto.cover());


        assertThrows(NotFoundCoverTypeException.class, () -> boardService.updateBoard(memberId, boardId, requestDto));

        // verify: coverValidator가 호출되었는지 확인
        verify(coverValidator, times(1)).validateCoverTypeIsValid(requestDto.cover());
    }


    @Test
    @DisplayName("보드 수정 시 커버가 존재하는데, 커버에 value 필드가 없으면 NotFoundCoverValueException 발생")
    void testUpdateBoard_MissingCoverValue() {
        // given
        Long boardId = 1L;
        Long memberId = 1L;

        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto("보드 이름",
                new CoverDto("COLOR", null),
                "PRIVATE");

        // Mock: validateCoverValueIsEmpty에서 커버에 value 필드가 없으면 예외 발생
        doThrow(new NotFoundCoverValueException()).when(coverValidator).validateCoverTypeIsValid(requestDto.cover());

        // when & then: 예외가 발생하는지 확인
        assertThrows(NotFoundCoverValueException.class, () -> boardService.updateBoard(memberId, boardId, requestDto));

        // verify: coverValidator가 호출되었는지 확인
        verify(coverValidator, times(1)).validateCoverTypeIsValid(requestDto.cover());
    }

    @ParameterizedTest
    @DisplayName("보드 수정 성공 테스트 - 로그 기록 모킹")
    @CsvSource({
            "'Board Name', '{\"type\":\"COLOR\",\"value\":\"#ffffff\"}', 'WORKSPACE'",   // 정상 케이스
            "'Another Board Name', '{\"type\":\"IMAGE\",\"value\":\"https://example.com/image.jpg\"}', 'PRIVATE'",
            "'Final Test Board', '{\"type\":\"IMAGE\",\"value\":\"https://example.com/final.jpg\"}', 'PRIVATE'"
            // 다양한 커버와 가시성 케이스
    })
    void testUpdateBoard_Success(String name, String coverJson, String visibility) {
        // given
        Long boardId = 1L;
        Long memberId = 1L;
        Map<String, Object> cover = coverJson.isEmpty() ? null : Map.of("type", "COLOR", "value", "#ffffff");

        // BoardUpdateRequestDto 생성
        BoardUpdateRequestDto requestDto = new BoardUpdateRequestDto(
                name, new CoverDto((String) cover.get("type"), (String) cover.get("value")), visibility);

        // Mock 설정
        Board mockBoard = mock(Board.class);
        WorkSpace mockWorkSpace = mock(WorkSpace.class);

        // Board와 WorkSpace 관련 Mock 메서드 설정
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(mockBoard));
        when(boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, memberId))
                .thenReturn(Optional.of(new BoardMember(1L, null, null, Authority.ADMIN, true, false)));
        when(mockBoard.getWorkSpace()).thenReturn(mockWorkSpace);
        when(mockWorkSpace.getName()).thenReturn("Mocked WorkSpace");
        when(mockBoard.updateBoardByAdmin(requestDto)).thenReturn(mockBoard);
        when(mockBoard.getName()).thenReturn(name);

        // Validator 메서드 호출 시 아무 일도 하지 않도록 설정
//        doNothing().when(boardValidator).validateVisibilityIsPresent(requestDto);
        doNothing().when(boardValidator).validateVisibilityIsValid(requestDto);

        // 커버가 있을 때만 커버 타입 검증하도록 설정
        if (cover != null) {
            doNothing().when(coverValidator).validateCoverTypeIsValid(requestDto.cover());
        }

        // Member 및 BoardHistory 관련 데이터 모킹
        Member mockMember = new Member(memberId, "Test User", "user@example.com");
        when(memberRepository.findByIdAndIsDeletedFalse(memberId)).thenReturn(Optional.of(mockMember));
        when(boardHistoryRepository.save(any())).thenReturn(null);

        // when
//        Long memberId = 1L;
//        when(boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, memberId)).thenReturn(
//                Optional.of(new BoardMember(1L, null, null, Authority.ADMIN, true, false)));
        Board updatedBoard = boardService.updateBoard(memberId, boardId, requestDto);

        // then
        assertEquals(mockBoard, updatedBoard);  // 업데이트된 보드가 mockBoard여야 함
//        verify(boardValidator).validateVisibilityIsPresent(requestDto);
        verify(boardValidator).validateVisibilityIsValid(requestDto);

        // 커버가 존재하는 경우에만 커버 검증 호출 확인
        if (cover != null) {
            verify(coverValidator, times(1)).validateCoverTypeIsValid(requestDto.cover());
        } else {
            verify(coverValidator, never()).validateCoverTypeIsValid(requestDto.cover());
        }

        // 보드 수정 로직이 호출되었는지 확인
        verify(mockBoard, times(1)).updateBoardByAdmin(requestDto);

        // BoardHistory 저장이 호출되었는지 확인
        verify(boardHistoryRepository).save(any(BoardHistory.class));
    }

    @ParameterizedTest
    @DisplayName("아카이브된 보드 리스트 조회 성공 테스트")
    @CsvSource({
            "1",
            "2"
    })
    void testGetArchivedBoards_Success(Long workspaceId) {
        // given: 모킹된 아카이브된 보드 리스트 생성
        List<Board> archivedBoards = Arrays.asList(
                Board.builder().id(1L).name("Board 1").isArchived(true).build(),
                Board.builder().id(2L).name("Board 2").isArchived(true).build()
        );

        // Mock: boardRepository.findAllByWorkSpaceIdAndIsArchivedTrue 호출 시 모킹된 보드 리스트 반환
        when(boardRepository.findAllByWorkSpaceIdAndIsArchivedTrueAndIsDeletedFalse(workspaceId)).thenReturn(archivedBoards);

        // when: 아카이브된 보드 리스트 조회
        List<Board> result = boardService.getArchivedBoards(workspaceId);

        // then: 반환된 리스트가 모킹된 보드 리스트와 동일한지 확인
        assertEquals(archivedBoards.size(), result.size());
        assertEquals(archivedBoards.get(0).getName(), result.get(0).getName());
        assertEquals(archivedBoards.get(1).getName(), result.get(1).getName());
        verify(boardRepository, times(1)).findAllByWorkSpaceIdAndIsArchivedTrueAndIsDeletedFalse(workspaceId);
    }


    @ParameterizedTest
    @DisplayName("보드 아카이브 상태 변경 성공 테스트")
    @CsvSource({
            "1, true",
            "2, false"
    })
    void testUpdateArchiveStatus_Success(Long boardId, boolean isArchived) {
        // given: 보드 모킹
        Board board = Board.builder()
                .id(boardId)
                .name("Test Board")
                .isArchived(isArchived)
                .build();
        Member member = new Member(1L , "시현", "sisi@naver.com");

        // Mock: getBoard 호출 시 모킹된 보드 반환
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(board));

        // when: 보드 아카이브 상태 변경
        boardService.changeArchiveStatus(member, boardId);

        // then: 보드의 아카이브 상태가 변경된 값인지 확인
        assertEquals(!isArchived, board.getIsArchived());
        verify(boardRepository, times(1)).findByIdAndIsDeletedFalse(boardId);
    }

    /**
     * 보드별 댓글 조회 TEST -------------------------------------------------------------------
     */
    @Test
    @DisplayName("보드별 댓글 조회 성공 테스트")
    void getAllRepliesForBoard_success() {
        // Given
        Long boardId = 1L;

        // 1. Member 엔티티 생성
        Member member = new Member(1L, "testUser", "user@example.com", "1111", "profileImgUrl");

        // 2. Board 객체 생성
        Board board = Board.builder()
                .id(boardId)
                .name("보드 1")
                .build();

        // 3. List 엔티티 생성
        com.narara.superboard.list.entity.List list = com.narara.superboard.list.entity.List.builder()
                .id(10001L)
                .name("Test List")
                .board(board)
                .build();

        // 4. Card 엔티티 생성 및 List 연결
        Card card = Card.builder()
                .id(1001L)
                .name("Test Card")
                .list(list) // 카드에 리스트 추가
                .build();

        // 5. Reply 엔티티 생성 및 Card, Member 연결
        Reply reply = Reply.builder()
                .id(1L)
                .content("This is a comment") // 테스트에서 검증할 댓글 내용
                .isDeleted(false)
                .member(member) // 댓글에 멤버 추가
                .card(card) // 댓글에 카드 추가
                .build();

        // Pageable 설정 및 Mock repository behavior
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reply> replyPage = new PageImpl<>(Collections.singletonList(reply), pageable, 1);
//        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(board));
        when(replyRepository.findAllByBoardId(boardId, pageable)).thenReturn(replyPage);
        when(boardRepository.existsById(any())).thenReturn(true);

        // When
        PageBoardReplyResponseDto responseDto = boardService.getRepliesByBoardId(boardId, pageable);

        // Then
        assertNotNull(responseDto);
        assertEquals(1, responseDto.totalElements());
        assertEquals(1, responseDto.boardReplyCollectionResponseDtos().size());
        assertEquals("This is a comment", responseDto.boardReplyCollectionResponseDtos().get(0).content());
        assertEquals("user@example.com", responseDto.boardReplyCollectionResponseDtos().get(0).email());
        assertEquals("testUser", responseDto.boardReplyCollectionResponseDtos().get(0).nickname());
        assertEquals("profileImgUrl", responseDto.boardReplyCollectionResponseDtos().get(0).profileImgUrl());
        assertEquals(1001L, responseDto.boardReplyCollectionResponseDtos().get(0).cardId());
        assertEquals("Test Card", responseDto.boardReplyCollectionResponseDtos().get(0).cardName());
        assertEquals(10001L, responseDto.boardReplyCollectionResponseDtos().get(0).listId());
        assertEquals("Test List", responseDto.boardReplyCollectionResponseDtos().get(0).listName());
    }

    @Test
    @DisplayName("보드 ID가 유효하지 않을 때 댓글 조회 실패 테스트")
    void getAllRepliesForBoard_boardNotFound() {
        // Given
        Long invalidBoardId = 999L; // 존재하지 않는 보드 ID

//        when(boardRepository.findByIdAndIsDeletedFalse(invalidBoardId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(BoardNotFoundException.class, () -> {
            boardService.getRepliesByBoardId(invalidBoardId, PageRequest.of(0, 10));
        });

        assertEquals("ID가 999인 보드를 찾을 수 없습니다.(이)가 존재하지 않습니다. ID가 999인 보드를 찾을 수 없습니다.(을)를 작성해주세요.",
                exception.getMessage());
    }


    @Test
    @DisplayName("보드에 댓글이 없을 때 댓글 조회 성공하지만 빈 리스트 반환")
    void getAllRepliesForBoard_noReplies() {
        // Given
        Long boardId = 1L;

        // Pageable 설정 및 Mock repository behavior
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reply> emptyReplyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        // 필요한 Mock 설정
        when(boardRepository.existsById(boardId)).thenReturn(true); // 보드 존재 확인 모킹
        when(replyRepository.findAllByBoardId(boardId, pageable)).thenReturn(emptyReplyPage);

        // When
        PageBoardReplyResponseDto responseDto = boardService.getRepliesByBoardId(boardId, pageable);

        // Then
        assertNotNull(responseDto);
        assertTrue(responseDto.boardReplyCollectionResponseDtos().isEmpty(), "댓글 리스트가 비어 있어야 함");
        assertEquals(0, responseDto.totalElements());
        assertEquals(0, responseDto.totalPages());
    }


    @Test
    @DisplayName("MEMBER 권한으로 보드 업데이트 테스트")
    void testUpdateBoardByMember() {
        // given
        Long memberId = 1L;
        Long boardId = 2L;
        BoardUpdateRequestDto dto = new BoardUpdateRequestDto("보드 이름",
                new CoverDto("IMAGE","https://"), "WORKSPACE");


        // mock 설정
        when(boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, memberId))
                .thenReturn(Optional.of(boardMember));
        when(boardMember.getAuthority()).thenReturn(Authority.MEMBER);
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(board));

        // WorkSpace 객체를 생성하여 Board에 설정
        WorkSpace mockWorkSpace = WorkSpace.builder().name("Test WorkSpace").build();
        when(board.getWorkSpace()).thenReturn(mockWorkSpace); // getWorkSpace 호출 시 WorkSpace 반환
        when(board.updateBoardByMember(dto)).thenReturn(board);

        // memberRepository에서 Member 객체 반환 모킹
        Member mockMember = new Member(memberId, "테스트 사용자", "test@example.com");
        when(memberRepository.findByIdAndIsDeletedFalse(memberId)).thenReturn(Optional.of(mockMember));

        // boardHistoryRepository의 save 메서드를 BoardHistory 객체를 반환하도록 모킹
        BoardHistory mockBoardHistory = mock(BoardHistory.class);
        when(boardHistoryRepository.save(any(BoardHistory.class))).thenReturn(mockBoardHistory);

        // when
        Board updatedBoard = boardService.updateBoard(memberId, boardId, dto);

        // then
//        verify(boardValidator).validateVisibilityIsPresent(dto);
        verify(boardValidator).validateVisibilityIsValid(dto);
        verify(board).updateBoardByMember(dto);
        verify(board, never()).updateBoardByAdmin(dto);  // ADMIN 업데이트는 호출되지 않음

        // boardHistoryRepository의 save가 호출되었는지 확인
        verify(boardHistoryRepository, times(1)).save(any(BoardHistory.class));
    }

    @Test
    @DisplayName("ADMIN 권한으로 보드 업데이트 테스트")
    void testUpdateBoardByAdmin() {
        // given
        Long memberId = 1L;
        Long boardId = 2L;
        Map<String, Object> background = Map.of("type", "IMAGE", "value", "https://...");
        BoardUpdateRequestDto dto = new BoardUpdateRequestDto(
                "보드 이름",
                new CoverDto((String) background.get("type"), (String) background.get("value")),
                "WORKSPACE"
        );

        // Mock 설정
        when(boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, memberId))
                .thenReturn(Optional.of(boardMember));
        when(boardMember.getAuthority()).thenReturn(Authority.ADMIN);

        // Board 객체에 WorkSpace 설정
        WorkSpace mockWorkSpace = WorkSpace.builder().name("시현 워크스페이스").build();
        when(board.getWorkSpace()).thenReturn(mockWorkSpace);  // getWorkSpace 호출 시 mockWorkSpace 반환
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(board));

        // updateBoardByAdmin 실행 시 board 반환
        when(board.updateBoardByAdmin(dto)).thenReturn(board);
        when(board.getName()).thenReturn("보드 이름"); // 이 부분에서 String 반환

        // Mocking: memberRepository와 boardHistoryRepository 설정
        when(memberRepository.findByIdAndIsDeletedFalse(memberId)).thenReturn(Optional.of(new Member(memberId, "테스트 사용자", "test@example.com")));

        // boardHistoryRepository.save()가 실행될 때 아무 동작도 하지 않도록 설정 (실제 저장 동작 생략)
        doAnswer(invocation -> null).when(boardHistoryRepository).save(any(BoardHistory.class));

        // when
        Board updatedBoard = boardService.updateBoard(memberId, boardId, dto);

        // then
//        verify(boardValidator).validateVisibilityIsPresent(dto);
        verify(boardValidator).validateVisibilityIsValid(dto);
        verify(board).updateBoardByAdmin(dto);
        verify(board, never()).updateBoardByMember(dto);  // MEMBER 업데이트는 호출되지 않음
    }


    @Test
    @DisplayName("잘못된 권한으로 보드 업데이트 시 AccessDeniedException 발생")
    void testUpdateBoardWithInvalidAuthority() {
        // given
        Long memberId = 1L;
        Long boardId = 2L;
        BoardUpdateRequestDto dto = mock(BoardUpdateRequestDto.class);

        // mock 설정 - BoardMember만 존재하도록 설정 (Board 자체는 필요하지 않음)
        when(boardMemberRepository.findFirstByBoard_IdAndMember_Id(boardId, memberId)).thenReturn(Optional.of(boardMember));

        // 잘못된 권한 설정
        when(boardMember.getAuthority()).thenReturn(null);  // 잘못된 권한

        // when & then
        assertThrows(AccessDeniedException.class, () -> boardService.updateBoard(memberId, boardId, dto));
    }

}
