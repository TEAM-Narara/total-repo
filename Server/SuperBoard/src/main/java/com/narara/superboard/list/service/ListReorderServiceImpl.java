package com.narara.superboard.list.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.interfaces.dto.ListMoveResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ListReorderServiceImpl implements ListReorderService {

    // 9223 개 생성 가능. / 최대 54번까지 중간값을 삽입가능
    private static final long DEFAULT_TOP_ORDER = 3_000_000_000_000_000_000L;
    private static final long REORDER_GAP = 100_000_000_000_000_000L;

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
        return orderInfoList;
    }
}
