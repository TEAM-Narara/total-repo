package com.narara.superboard.list.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastructure.BoardHistoryRepository;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundNameException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;
import com.narara.superboard.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@DisplayName("리스트 서비스에 대한 단위 테스트")
class ListServiceImplTest implements MockSuperBoardUnitTests {

    @Mock
    private NameValidator nameValidator;

    @Mock
    private LastOrderValidator lastOrderValidator;

    @Mock
    private ListRepository listRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardHistoryRepository boardHistoryRepository;

    @Mock
    private BoardService boardService;

    @InjectMocks
    private ListServiceImpl listService;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("리스트 이름이 비어있을 때 실패")
    void shouldFailWhenListNameIsEmpty_UnitTest(String listName) {
        // given
        ListCreateRequestDto requestDto = new ListCreateRequestDto(1L, listName);
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // requestDto가 전달될 때만 NotFoundNameException을 발생시키도록 NameValidator를 모의합니다.
        doThrow(new NotFoundNameException("리스트"))
                .when(nameValidator).validateListNameIsEmpty(requestDto);

        // when & then
        NotFoundNameException exception = assertThrows(NotFoundNameException.class, () -> {
            listService.createList(member, requestDto);
        });

        assertEquals("리스트의 이름(이)가 존재하지 않습니다. 이름(을)를 작성해주세요.", exception.getMessage());
        verify(nameValidator).validateListNameIsEmpty(any(ListCreateRequestDto.class)); // 검증 추가
        verify(nameValidator, times(1)).validateListNameIsEmpty(requestDto);
    }


    @Test
    @DisplayName("리스트 생성 성공 테스트")
    void testCreateListSuccess() {
        // given
        Long boardId = 1L;
        String listName = "Valid List Name";
        Long lastListOrder = 10L;
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Mocking ListCreateRequestDto
        ListCreateRequestDto requestDto = new ListCreateRequestDto(boardId, listName);

        // Mocking Board entity
        Board board = mock(Board.class);
        when(board.getLastListOrder()).thenReturn(lastListOrder);
        when(boardRepository.getReferenceById(boardId)).thenReturn(board);

        // Mocking List entity
        List savedList = mock(List.class);
        when(savedList.getId()).thenReturn(1L);
        when(listRepository.save(any(List.class))).thenReturn(savedList);

        // Mocking NameValidator and LastOrderValidator behavior
        doNothing().when(nameValidator).validateListNameIsEmpty(requestDto);
        doNothing().when(lastOrderValidator).checkValidListLastOrder(board);

        // when
        List resultList = listService.createList(member, requestDto);

        // then
        assertEquals(savedList.getId(), resultList.getId());
        verify(nameValidator, times(1)).validateListNameIsEmpty(requestDto);
        verify(lastOrderValidator, times(1)).checkValidListLastOrder(board);
        verify(boardRepository, times(1)).getReferenceById(boardId);
        verify(listRepository, times(1)).save(any(List.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Updated List Name", "New List Name"})
    @DisplayName("리스트 업데이트 성공 테스트")
    void testUpdateListSuccess(String newName) {
        // given
        Long listId = 1L;
        ListUpdateRequestDto requestDto = ListUpdateRequestDto.builder()
                .listId(listId)
                .listName(newName)
                .build();
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Mocking List entity
        List list = mock(List.class);
        Board board = mock(Board.class); // Board 객체를 모킹
        when(listRepository.findById(listId)).thenReturn(Optional.of(list));
        when(list.getBoard()).thenReturn(board); // list.getBoard()가 board를 반환하도록 설정

        // Mocking the board member list behavior to return a list of board members
        when(board.getBoardMemberList()).thenReturn(Arrays.asList(
                new BoardMember(member, Authority.ADMIN) // BoardMember 객체를 생성하여 포함
        ));

        // when
        List updatedList = listService.updateList(member, listId, requestDto);

        // then
        verify(listRepository, times(1)).findById(listId);
        verify(list, times(1)).updateList(requestDto);
        verify(nameValidator, times(1)).validateListNameIsEmpty(requestDto);
        assertEquals(list, updatedList);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @DisplayName("리스트 ID가 존재하지 않을 때 실패")
    void testUpdateListFailureListNotFound(Long listId) {
        // given
        ListUpdateRequestDto requestDto = ListUpdateRequestDto.builder()
                .listId(listId)
                .listName("Valid Name")
                .build();
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Mocking: 리스트를 찾지 못했을 때 예외 발생
        when(listRepository.findById(listId)).thenReturn(Optional.empty());

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> {
            listService.updateList(member, listId, requestDto);
        });

        assertEquals("해당하는 리스트(이)가 존재하지 않습니다. 리스트ID: " + listId, exception.getMessage());
        verify(listRepository, times(1)).findById(listId);
        verify(nameValidator, never()).validateListNameIsEmpty(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"New Valid Name"})
    @DisplayName("리스트 업데이트 시 검증 성공 테스트")
    void testUpdateListValidationSuccess(String newName) {
        // given
        Long listId = 1L;
        ListUpdateRequestDto requestDto = ListUpdateRequestDto.builder()
                .listId(listId)
                .listName(newName)
                .build();
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Mocking List entity
        List list = mock(List.class);
        Board board = mock(Board.class); // Board 객체를 모킹
        when(listRepository.findById(listId)).thenReturn(Optional.of(list));
        when(list.getBoard()).thenReturn(board); // list.getBoard()가 board를 반환하도록 설정

        // Mocking the board member list behavior to return a list of board members
        when(board.getBoardMemberList()).thenReturn(Arrays.asList(
                new BoardMember(member, Authority.ADMIN) // BoardMember 객체를 생성하여 포함
        ));
        // when
        List updatedList = listService.updateList(member, listId, requestDto);

        // then
        verify(listRepository, times(1)).findById(listId);
        verify(list, times(1)).updateList(requestDto);
        verify(nameValidator, times(1)).validateListNameIsEmpty(requestDto);
        assertEquals(list, updatedList);
    }

    @Test
    @DisplayName("리스트 아카이브 상태 변경 성공")
    void testChangeListIsArchivedSuccess() {
        // given
        Long listId = 1L;
        List list = mock(List.class);
        Board board = mock(Board.class);
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Mocking the repository behavior
        when(listRepository.findById(listId)).thenReturn(Optional.of(list));
        when(list.getBoard()).thenReturn(board);
        when(board.getBoardMemberList()).thenReturn(Arrays.asList(
                new BoardMember(member, Authority.ADMIN) // assuming BoardMember constructor with Board and Member
        ));
        // when
        List updatedList = listService.changeListIsArchived(member, listId);

        // then
        verify(listRepository, times(1)).findById(listId);
        verify(list, times(1)).changeListIsArchived(); // 리스트 아카이브 변경 호출 확인
        assertEquals(list, updatedList); // 반환된 리스트가 같은지 확인
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @DisplayName("보드가 존재하지 않을 때 예외 발생")
    void testGetArchivedListBoardNotFound(Long boardId) {
        // given
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.empty());
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> {
            listService.getArchivedList(member, boardId);
        });

        // then
        assertEquals(exception.getMessage(), "해당하는 보드(이)가 존재하지 않습니다. 보드ID: " + boardId);

        verify(boardRepository, times(1)).findByIdAndIsDeletedFalse(boardId);
        verify(listRepository, never()).findByBoardAndIsArchivedAndIsDeletedFalse(any(Board.class), anyBoolean());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @DisplayName("아카이브된 리스트가 없을 때 빈 리스트 반환")
    void testGetArchivedListNoArchivedLists(Long boardId) {
        // given
        Board board = mock(Board.class);
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(board));
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // 리스트가 비어있는 경우를 처리
        when(listRepository.findByBoardAndIsArchivedAndIsDeletedFalse(board, true)).thenReturn(Collections.emptyList());

        // when
        java.util.List<List> archivedLists = listService.getArchivedList(member, boardId);

        // then
        assertNotNull(archivedLists);
        assertTrue(archivedLists.isEmpty());

        verify(boardRepository, times(1)).findByIdAndIsDeletedFalse(boardId);
        verify(listRepository, times(1)).findByBoardAndIsArchivedAndIsDeletedFalse(board, true);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @DisplayName("아카이브된 리스트가 있을 때 단일 리스트 조회 성공")
    void testGetArchivedListSuccess_SingleItem(Long boardId) {
        // given
        Board board = mock(Board.class);
        List archivedList = mock(List.class); // 단일 리스트를 모킹
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(board));
        when(listRepository.findByBoardAndIsArchivedAndIsDeletedFalse(board, true))
                .thenReturn(Collections.singletonList(archivedList)); // 단일 아카이브 리스트 반환
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // when
        java.util.List<List> archivedLists = listService.getArchivedList(member, boardId);

        // then
        assertNotNull(archivedLists);
        assertEquals(1, archivedLists.size()); // 리스트에 하나의 아카이브된 항목이 있는지 확인
        assertEquals(archivedList, archivedLists.get(0));

        verify(boardRepository, times(1)).findByIdAndIsDeletedFalse(boardId);
        verify(listRepository, times(1)).findByBoardAndIsArchivedAndIsDeletedFalse(board, true);
    }

    @Test
    @DisplayName("아카이브된 리스트가 여러 개 있을 때 조회 성공")
    void testGetArchivedListSuccess_MultipleItems() {
        // given
        Long boardId = 1L;
        Board board = mock(Board.class);
        List archivedList1 = mock(List.class);
        List archivedList2 = mock(List.class);
        when(boardRepository.findByIdAndIsDeletedFalse(boardId)).thenReturn(Optional.of(board));
        when(listRepository.findByBoardAndIsArchivedAndIsDeletedFalse(board, true))
                .thenReturn(Arrays.asList(archivedList1, archivedList2)); // 두 개의 아카이브 리스트 반환
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // when
        java.util.List<List> archivedLists = listService.getArchivedList(member, boardId);

        // then
        assertNotNull(archivedLists);
        assertEquals(2, archivedLists.size()); // 리스트에 두 개의 아카이브된 항목이 있는지 확인
        assertEquals(archivedList1, archivedLists.get(0));
        assertEquals(archivedList2, archivedLists.get(1));

        verify(boardRepository, times(1)).findByIdAndIsDeletedFalse(boardId);
        verify(listRepository, times(1)).findByBoardAndIsArchivedAndIsDeletedFalse(board, true);
    }
}
