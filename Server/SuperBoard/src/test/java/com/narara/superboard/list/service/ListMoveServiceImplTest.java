package com.narara.superboard.list.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListMoveResult;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("리스트 이동 서비스에 대한 단위 테스트")
class ListMoveServiceImplTest implements MockSuperBoardUnitTests {
    @Mock
    private ListRepository listRepository;

    @Mock
    private ListReorderService listReorderService;

    @Mock
    private ListService listService;

    @InjectMocks
    private ListMoveServiceImpl listMoveService;

    private Member member;
    private WorkSpace workspace;
    private Board board;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .nickname("마루")
                .email("maru@gmail.com")
                .profileImgUrl("http~~")
                .password("1234")
                .build();

        workspace = WorkSpace.createWorkSpace(
                new WorkSpaceCreateRequestDto("새로운 워크")
        );

        board = Board.createBoard(
                new BoardCreateRequestDto(
                        workspace.getId(),
                        "보듀",
                        "WORKSPACE",
                        null,
                        false
                ),
                workspace
        );
    }

    @Test
    @DisplayName("한 리스트 내부: 맨 앞으로 이동 테스트")
    void testMoveToTheFront() {
        List list1 = List.builder()
                .id(1L)
                .board(board)
                .name("이름1")
                .myOrder(1L)
                .build();

        List list2 = List.builder()
                .id(2L)
                .board(board)
                .name("이름1")
                .myOrder(2L)
                .build();

        List list3 = List.builder()
                .id(3L)
                .board(board)
                .name("이름1")
                .myOrder(3L)
                .build();

        when(listRepository.findById(list3.getId())).thenReturn(Optional.of(list3));

        ArrayList<List> existsResult = new ArrayList<>(java.util.List.of(list1, list2, list3));
        when(listRepository.findAllByBoardOrderByMyOrderAsc(board)).thenReturn(existsResult);

        ListMoveResult.ReorderedListMove listMoveResult = (ListMoveResult.ReorderedListMove) listMoveService.moveListVersion1(member, list3.getId(), 1L);

        assertEquals(listMoveResult.orderInfos().size(), 3);
        assertEquals(listMoveResult.orderInfos().get(0).listId(), 3);
        assertEquals(listMoveResult.orderInfos().get(0).myOrder(), 1);
        assertEquals(listMoveResult.orderInfos().get(1).listId(), 1);
        assertEquals(listMoveResult.orderInfos().get(1).myOrder(), 2);
        assertEquals(listMoveResult.orderInfos().get(2).listId(), 2);
        assertEquals(listMoveResult.orderInfos().get(2).myOrder(), 3);
    }
}