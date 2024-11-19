package com.narara.superboard.list.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.interfaces.dto.ListMoveResponseDto;

public interface ListReorderService {
    java.util.List<ListMoveResponseDto> reorderAllListOrders(Board board, List targetList, int targetIndex);
}
