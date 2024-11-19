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
    public java.util.List<ListMoveResponseDto> reorderAllListOrders(Board board, List targetList, int targetIndex) {
        java.util.List<List> lists = listRepository.findAllByBoardOrderByMyOrderAsc(board);

        // targetIndex -1일 경우 가장 아래로 삽입
        if (targetIndex == -1) {
            lists.remove(targetList);
            lists.add(targetList);  // 가장 마지막에 삽입
        } else {
            lists.remove(targetList);
            lists.add(targetIndex, targetList);  // 특정 위치에 삽입
        }

        // 고정된 간격으로 리스트 순서 재설정
        long newOrder = DEFAULT_TOP_ORDER;
        java.util.List<ListMoveResponseDto> orderInfoList = new ArrayList<>();
        for (List list : lists) {
            list.setMyOrder(newOrder);
            orderInfoList.add(new ListMoveResponseDto(list.getId(), newOrder));
            newOrder += REORDER_GAP;
        }

        listRepository.saveAll(lists);
        board.setLastListOrder(newOrder - REORDER_GAP);

        return orderInfoList;
    }


}
