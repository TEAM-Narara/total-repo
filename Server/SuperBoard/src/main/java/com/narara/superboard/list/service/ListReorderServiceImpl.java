package com.narara.superboard.list.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListMoveResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static com.narara.superboard.common.constant.MoveConst.*;

@Service
@RequiredArgsConstructor
public class ListReorderServiceImpl implements ListReorderService {

    private final ListRepository listRepository;

    @Override
    @Transactional
    public java.util.List<ListMoveResponseDto> reorderAllListOrders(Board board) {
        java.util.List<List> lists = listRepository.findAllByBoardOrderByMyOrderAsc(board);

        long newOrder = DEFAULT_TOP_ORDER;
        java.util.List<ListMoveResponseDto> orderInfoList = new ArrayList<>();

        for (List list : lists) {
            list.setMyOrder(newOrder);
            orderInfoList.add(new ListMoveResponseDto(list.getId(), newOrder));
            newOrder += REORDER_GAP; // 고정된 간격을 사용하여 순서를 증가시킴
        }

        listRepository.saveAll(lists);
        board.setLastListOrder(newOrder);

        return orderInfoList;
    }
}
