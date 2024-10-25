package com.narara.superboard.list.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastrucuture.BoardRepository;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundNameException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastrucure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

@DisplayName("ListServiceImpl 테스트")
class ListServiceImplTest implements MockSuperBoardUnitTests {

    @Mock
    private NameValidator nameValidator;

    @Mock
    private LastOrderValidator lastOrderValidator;

    @Mock
    private ListRepository listRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private ListServiceImpl listService;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("리스트 이름이 비어있을 때 실패")
    void shouldFailWhenListNameIsEmpty_UnitTest(String listName) {
        // given
        ListCreateRequestDto requestDto = new ListCreateRequestDto(1L, listName);

        // Mocking NameValidator behavior to throw NotFoundNameException
        doThrow(new NotFoundNameException("리스트"))
                .when(nameValidator).validateListNameIsEmpty(any(ListCreateRequestDto.class));

        // when & then
        NotFoundNameException exception = assertThrows(NotFoundNameException.class, () -> {
            listService.createList(requestDto);
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
        doNothing().when(lastOrderValidator).checkValidListLastOrder(lastListOrder);

        // when
        List resultList = listService.createList(requestDto);

        // then
        assertEquals(savedList.getId(), resultList.getId());
        verify(nameValidator, times(1)).validateListNameIsEmpty(requestDto);
        verify(lastOrderValidator, times(1)).checkValidListLastOrder(lastListOrder);
        verify(boardRepository, times(1)).getReferenceById(boardId);
        verify(listRepository, times(1)).save(any(List.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("리스트 이름이 비어있을 때 실패")
    void testUpdateListFailureEmptyName(String emptyName) {
        // given
        Long listId = 1L;
        ListUpdateRequestDto requestDto = ListUpdateRequestDto.builder()
                .listId(listId)
                .listName(emptyName)
                .build();

        // Mocking List entity
        List list = mock(List.class);
        when(listRepository.findById(listId)).thenReturn(Optional.of(list));

        // Mocking: 리스트 이름이 비어있을 때 예외 발생
        doThrow(new IllegalArgumentException("리스트 이름이 비어있습니다."))
                .when(nameValidator).validateListNameIsEmpty(requestDto);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            listService.updateList(listId, requestDto);
        });

        assertEquals("리스트 이름이 비어있습니다.", exception.getMessage());
        verify(nameValidator, times(1)).validateListNameIsEmpty(requestDto);
        verify(list, never()).updateList(requestDto);
    }

}
