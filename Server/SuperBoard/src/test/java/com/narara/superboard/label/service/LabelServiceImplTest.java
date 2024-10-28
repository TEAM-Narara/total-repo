package com.narara.superboard.label.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.infrastructure.BoardRepository;
import com.narara.superboard.common.application.validator.ColorValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.label.infrastructure.LabelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto("Test Label", 0xFFFFFF00L);

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
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto("Test Label", color);

        // Mocking: colorValidator가 유효하지 않은 색상에 대해 예외를 던지도록 설정
        doThrow(new IllegalArgumentException("Invalid color format")).when(colorValidator).validateLabelColor(requestDto);

        // then
        assertThrows(IllegalArgumentException.class, () -> labelService.createLabel(1L, requestDto));
    }

}