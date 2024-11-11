package com.narara.superboard.list.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListMoveServiceImpl implements ListMoveService {

    private final ListRepository listRepository;

    private static final long DEFAULT_TOP_ORDER = 1_0000_000_000L; // 아무 것도 없는 상황에서 처음 수.
    private static final double MOVE_TOP_ORDER_RATIO = 2.0 / 3.0;
    private static final double MOVE_BOTTOM_ORDER_RATIO = 1.0 / 3.0;
    private static final long LARGE_INCREMENT = 1_000_000_000L;

    @Override
    @Transactional
    public void moveListToTop(Long listId) {
        // 보드와 리스트를 조회
        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
        Board board = targetList.getBoard();
        // 현재 보드에서 가장 작은 순서 값을 가져옴
        Optional<List> topList = listRepository.findFirstByBoardOrderByMyOrderAsc(board);

        // topList가 있을 경우 첫 번째 순서 범위의 2/3 지점으로, 없으면 기본값 3억으로 설정
        Long newOrder = topList
                .map(list -> Math.round(list.getMyOrder() * MOVE_TOP_ORDER_RATIO))
                .orElse(DEFAULT_TOP_ORDER);

        // 새로운 myOrder 값으로 설정하여 맨 앞으로 이동
        targetList.setMyOrder(newOrder); // setter가 필요할 경우 추가해야 합니다.

        if (topList.isEmpty()) {
            board.setLastListOrder(newOrder);
        }
    }

    @Override
    @Transactional
    public void moveListToBottom(Long listId) {
        // 보드와 리스트를 조회
        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
        Board board = targetList.getBoard();

        // 현재 보드에서 가장 큰 순서 값을 가져오고, 없으면 기본값(DEFAULT_TOP_ORDER)을 사용하여 이동
        Long newOrder = listRepository.findFirstByBoardOrderByMyOrderDesc(board)
                .map(lastList -> {
                    // Long.MAX_VALUE와 마지막 리스트의 myOrder 사이의 MOVE_BOTTOM_ORDER_RATIO 지점 계산
                    long maxLimit = lastList.getMyOrder() +
                            Math.round((Long.MAX_VALUE - lastList.getMyOrder()) * MOVE_BOTTOM_ORDER_RATIO);
                    // 1억 더한 값과 위의 계산 값 중 더 작은 값 선택
                    return Math.min(lastList.getMyOrder() + LARGE_INCREMENT, maxLimit);
                })
                .orElse(DEFAULT_TOP_ORDER);

        // 새로운 myOrder 값으로 설정하여 맨 뒤로 이동
        targetList.setMyOrder(newOrder); // setter가 필요할 경우 추가해야 합니다.
        board.setLastListOrder(newOrder);
    }

    @Override
    @Transactional
    public void moveListBetween(Long listId, Long previousListId, Long nextListId) {
        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
        List previousList = listRepository.findById(previousListId)
                .orElseThrow(() -> new NotFoundEntityException(previousListId, "이전 리스트"));
        List nextList = listRepository.findById(nextListId)
                .orElseThrow(() -> new NotFoundEntityException(nextListId, "다음 리스트"));

        // 두 리스트의 중간 위치 계산
        Long newOrder = (previousList.getMyOrder() + nextList.getMyOrder()) / 2;

        // 새로운 myOrder 값으로 설정하여 중간에 위치
        targetList.setMyOrder(newOrder);
    }
}
