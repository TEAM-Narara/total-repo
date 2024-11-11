package com.narara.superboard.list.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import com.narara.superboard.list.interfaces.dto.ListMoveResponseDto;
import com.narara.superboard.list.interfaces.dto.ListMoveResult;
import com.narara.superboard.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.narara.superboard.list.ListAction.MOVE_LIST;
import static com.narara.superboard.common.constant.MoveConst.*;

@Service
@RequiredArgsConstructor
public class ListMoveServiceImpl implements ListMoveService {

    private final ListRepository listRepository;
    private final ListReorderService listReorderService; // ListReorderService 주입
    private final ListService listService; // ListReorderService 주입


    @Override
    @Transactional
    public ListMoveResult moveListToTop(Member member, Long listId) {
        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
        listService.checkBoardMember(targetList, member, MOVE_LIST);

        Board board = targetList.getBoard();
        Optional<List> topList = listRepository.findFirstByBoardOrderByMyOrderAsc(board);

        // 대상 리스트가 이미 최상위에 위치한 경우
        if (topList.isPresent() && targetList.getMyOrder().equals(topList.get().getMyOrder())) {
            return new ListMoveResult.SingleListMove(new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }
        // topList가 있을 경우 비율 기반 순서로, 없으면 기본 순서 값으로 설정
        long baseOrder = topList.map(list -> Math.round(list.getMyOrder() * MOVE_TOP_ORDER_RATIO))
                .orElse(DEFAULT_TOP_ORDER);

        // 고유한 순서 값 생성 후 결과 반환
        java.util.List<ListMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(board, baseOrder);
        if (orderInfoList.size() > 1) {
            return new ListMoveResult.ReorderedListMove(orderInfoList);
        }

        // 새로운 순서 적용 및 보드 마지막 리스트 순서 업데이트
        targetList.setMyOrder(orderInfoList.getFirst().myOrder());
        if (topList.isEmpty()) {
            board.setLastListOrder(orderInfoList.getFirst().myOrder());
        }

        return new ListMoveResult.SingleListMove(orderInfoList.getFirst());
    }

    @Override
    @Transactional
    public ListMoveResult moveListToBottom(Member member, Long listId) {
        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
        listService.checkBoardMember(targetList, member, MOVE_LIST);

        Board board = targetList.getBoard();
        Optional<List> bottomList = listRepository.findFirstByBoardOrderByMyOrderDesc(board);

        // 대상 리스트가 이미 최하위에 위치한 경우
        if (bottomList.isPresent() && targetList.getMyOrder().equals(bottomList.get().getMyOrder())) {
            return new ListMoveResult.SingleListMove(new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }

        // 현재 보드에서 가장 큰 순서 값을 가져오고, 없으면 기본값 사용
        long baseOrder = bottomList.map(lastList -> {
            long maxLimit = lastList.getMyOrder() + Math.round((Long.MAX_VALUE - lastList.getMyOrder()) * MOVE_BOTTOM_ORDER_RATIO);
            return Math.min(lastList.getMyOrder() + LARGE_INCREMENT, maxLimit);
        }).orElse(DEFAULT_TOP_ORDER);

        // 고유한 newOrder 값을 재시도로 생성 및 재배치 체크
        java.util.List<ListMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(board, baseOrder);

        // 재배치가 필요한 경우 전체 리스트 반환
        if (orderInfoList.size() > 1) {
            return new ListMoveResult.ReorderedListMove(orderInfoList);
        }

        // 단일 리스트 정보 설정 및 반환
        targetList.setMyOrder(orderInfoList.getFirst().myOrder());
        board.setLastListOrder(orderInfoList.getFirst().myOrder());

        return new ListMoveResult.SingleListMove(orderInfoList.getFirst());
    }

    @Override
    @Transactional
    public ListMoveResult moveListBetween(Member member, Long listId, Long previousListId, Long nextListId) {
        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
        listService.checkBoardMember(targetList, member, MOVE_LIST);

        List previousList = listRepository.findById(previousListId)
                .orElseThrow(() -> new NotFoundEntityException(previousListId, "이전 리스트"));
        List nextList = listRepository.findById(nextListId)
                .orElseThrow(() -> new NotFoundEntityException(nextListId, "다음 리스트"));

        // 두 리스트의 중간 위치 계산
        long prevOrder = previousList.getMyOrder();
        long nextOrder = nextList.getMyOrder();
        long gap = nextOrder - prevOrder;

        // 현재 순서가 이전 및 다음 리스트 순서 사이에 있는 경우
        if (targetList.getMyOrder() > prevOrder && targetList.getMyOrder() < nextOrder) {
            return new ListMoveResult.SingleListMove(new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }

        // 간격이 MAX_GAP 이상일 경우 고정 간격을 적용하고, 아니면 중간값을 사용
        long baseOrder = (gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP)
                ? prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
                : (prevOrder + nextOrder) / HALF_DIVIDER;

        // 고유한 newOrder 값을 재시도로 생성 및 재배치 체크
        java.util.List<ListMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(targetList.getBoard(), baseOrder);

        // 재배치가 필요한 경우 전체 리스트 반환
        if (orderInfoList.size() > 1) {
            return new ListMoveResult.ReorderedListMove(orderInfoList);
        }

        // 단일 리스트 정보 설정 및 반환
        targetList.setMyOrder(orderInfoList.getFirst().myOrder());
        listRepository.save(targetList);

        return new ListMoveResult.SingleListMove(orderInfoList.getFirst());
    }

    // 고유성 보장을 위해 임의 간격 조정 로직 추가
    private long generateUniqueOrder(long baseOrder) {
        // 작은 기본 간격 값 설정
        long gap = LARGE_INCREMENT / 100; // LARGE_INCREMENT의 1%를 기본 간격으로 사용
        // System.nanoTime() 활용한 임의의 간격 추가
        long offset = System.nanoTime() % gap;
        return baseOrder + offset;
    }

    private java.util.List<ListMoveResponseDto> generateUniqueOrderWithRetry(Board board, long baseOrder) {
        int maxAttempts = 3;
        int attempt = 0;
        long newOrder = generateUniqueOrder(baseOrder);

        while (attempt < maxAttempts) {
            if (newOrder <= 0 || newOrder >= Long.MAX_VALUE) {
                return listReorderService.reorderAllListOrders(board);
            }

            if (!isOrderConflict(board, newOrder)) {
                return java.util.List.of(new ListMoveResponseDto(board.getId(), newOrder));
            } else {
                long randomOffset = ThreadLocalRandom.current().nextLong(50, 150);
                newOrder = baseOrder + (attempt + 1) * 100L + randomOffset;
                attempt++;
            }
        }
        throw new RuntimeException("최대 " + maxAttempts + "번의 시도 후에도 고유한 순서 값을 설정할 수 없습니다.");
    }

    // 리스트 순서 중복 확인 메서드
    private boolean isOrderConflict(Board board, long order) {
        return listRepository.existsByBoardAndMyOrder(board, order);
    }
}
