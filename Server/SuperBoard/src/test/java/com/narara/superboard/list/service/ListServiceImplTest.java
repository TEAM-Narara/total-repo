package com.narara.superboard.list.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastrucuture.BoardRepository;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.exception.NotFoundNameException;
import com.narara.superboard.list.infrastrucure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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

//    @ParameterizedTest
//    @NullAndEmptySource
//    @ValueSource(strings = {"  ", "\t", "\n"})
//    void shouldFailWhenListNameIsEmpty(String listName) {
//        // given
//        ListCreateRequestDto requestDto = new ListCreateRequestDto(1L, listName);
//
//        // when & then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            listService.createList(requestDto);
//        });
//
//        assertEquals("List name must not be empty", exception.getMessage());
//        verify(nameValidator).validateNameIsEmpty(requestDto);
//    }

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

}
