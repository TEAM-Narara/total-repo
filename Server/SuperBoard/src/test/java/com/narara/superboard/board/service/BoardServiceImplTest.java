package com.narara.superboard.board.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.infrastrucutre.BoardRepository;
import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.board.interfaces.dto.BoardDetailResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BoardServiceImplTest {

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("워크스페이스와 관련된 보드에 대한 조회 성공 테스트")
    void testGetBoardCollectionResponseDtoSuccess() {
        // given
        Long workSpaceId = 1L;

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

        // boardRepository의 findAllByWorkSpaceId 호출 시 mock 데이터를 반환
        when(boardRepository.findAllByWorkSpaceId(workSpaceId)).thenReturn(mockBoardList);

        // when
        BoardCollectionResponseDto result = boardService.getBoardCollectionResponseDto(workSpaceId);

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
}