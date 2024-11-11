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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.narara.superboard.list.ListAction.MOVE_LIST;
import static com.narara.superboard.common.constant.MoveConst.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListMoveServiceImpl implements ListMoveService {

    private final ListRepository listRepository;
    private final ListReorderService listReorderService; // ListReorderService 주입
    private final ListService listService; // ListReorderService 주입


    @Override
    @Transactional
    public ListMoveResult moveListToTop(Member member, Long listId) {
        log.info("moveListToTop 메서드 시작 - listId: {}", listId);

        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
        log.info("타겟 리스트 조회 완료 - targetListId: {}, currentOrder: {}", targetList.getId(), targetList.getMyOrder());

        listService.checkBoardMember(targetList, member, MOVE_LIST);
        log.info("보드 접근 권한 확인 완료 - memberId: {}, boardId: {}", member.getId(), targetList.getBoard().getId());

        Board board = targetList.getBoard();
        Optional<List> topList = listRepository.findFirstByBoardOrderByMyOrderAsc(board);

        log.info("탑 리스트 조회 완료 - targetListId: {}, currentOrder: {}", topList.get().getId(), topList.get().getMyOrder());

        if (topList.isPresent() && targetList.getMyOrder().equals(topList.get().getMyOrder())) {
            log.info("이미 리스트가 최상위에 위치 - listId: {}", targetList.getId());
            return new ListMoveResult.SingleListMove(new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }

        // 기준 순서 값 계산 (topList가 있을 경우 비율 기반 순서로, 없으면 기본 순서 값으로 설정)
        long baseOrder = topList.map(list -> {
            long maxLimit = Math.round(list.getMyOrder() * MOVE_TOP_ORDER_RATIO);
            long calculatedOrder = Math.max(list.getMyOrder() - LARGE_INCREMENT, maxLimit);
            log.info("기준 순서 값 계산 - topListOrder: {}, calculatedOrder: {}", list.getMyOrder(), calculatedOrder);
            return calculatedOrder;
        }).orElse(DEFAULT_TOP_ORDER);
        log.info("기준 순서 값 설정 - baseOrder: {}", baseOrder);

        java.util.List<ListMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(board, baseOrder);
        log.info("고유 순서 값 생성 및 재배치 체크 완료 - orderInfoList size: {}", orderInfoList.size());

        if (orderInfoList.size() > 1) {
            log.info("재배치 필요 - 전체 리스트 반환");
            return new ListMoveResult.ReorderedListMove(orderInfoList);
        }

        targetList.setMyOrder(orderInfoList.getFirst().myOrder());
        if (topList.isEmpty()) {
            board.setLastListOrder(orderInfoList.getFirst().myOrder());
        }
        log.info("리스트 최상위로 이동 완료 - newOrder: {}", orderInfoList.getFirst().myOrder());

        return new ListMoveResult.SingleListMove(orderInfoList.getFirst());
    }


    @Override
    @Transactional
    public ListMoveResult moveListToBottom(Member member, Long listId) {
        log.info("moveListToBottom 메서드 시작 - listId: {}", listId);

        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
        log.info("타겟 리스트 조회 완료 - targetListId: {}, currentOrder: {}", targetList.getId(), targetList.getMyOrder());

        listService.checkBoardMember(targetList, member, MOVE_LIST);
        log.info("보드 접근 권한 확인 완료 - memberId: {}, boardId: {}", member.getId(), targetList.getBoard().getId());

        Board board = targetList.getBoard();
        Optional<List> bottomList = listRepository.findFirstByBoardOrderByMyOrderDesc(board);

        // 대상 리스트가 이미 최하위에 위치한 경우
        if (bottomList.isPresent() && targetList.getMyOrder().equals(bottomList.get().getMyOrder())) {
            log.info("이미 리스트가 최하위에 위치 - listId: {}", targetList.getId());
            return new ListMoveResult.SingleListMove(new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }

        // 현재 보드에서 가장 큰 순서 값을 가져오고, 없으면 기본값 사용
        long baseOrder = bottomList.map(lastList -> {
            long maxLimit = lastList.getMyOrder() + Math.round((Long.MAX_VALUE - lastList.getMyOrder()) * MOVE_BOTTOM_ORDER_RATIO);
            long calculatedOrder = Math.min(lastList.getMyOrder() + LARGE_INCREMENT, maxLimit);
            log.info("순서 값 계산 - lastListOrder: {}, calculatedOrder: {}", lastList.getMyOrder(), calculatedOrder);
            return calculatedOrder;
        }).orElse(DEFAULT_TOP_ORDER);

        log.info("기준 순서 값 설정 - baseOrder: {}", baseOrder);

        // 고유한 newOrder 값을 재시도로 생성 및 재배치 체크
        java.util.List<ListMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(board, baseOrder);
        log.info("고유 순서 값 생성 및 재배치 체크 완료 - orderInfoList size: {}", orderInfoList.size());

        // 재배치가 필요한 경우 전체 리스트 반환
        if (orderInfoList.size() > 1) {
            log.info("재배치 필요 - 전체 리스트 반환");
            return new ListMoveResult.ReorderedListMove(orderInfoList);
        }

        // 단일 리스트 정보 설정 및 반환
        targetList.setMyOrder(orderInfoList.getFirst().myOrder());
        board.setLastListOrder(orderInfoList.getFirst().myOrder());
        log.info("리스트 최하위로 이동 완료 - newOrder: {}", orderInfoList.getFirst().myOrder());

        return new ListMoveResult.SingleListMove(orderInfoList.getFirst());
    }

    @Override
    @Transactional
    public ListMoveResult moveListBetween(Member member, Long listId, Long previousListId, Long nextListId) {
        log.info("moveListBetween 메서드 시작 - listId: {}, previousListId: {}, nextListId: {}", listId, previousListId, nextListId);

        // 동일한 ID가 있는지 확인하여, 동일한 경우 현재 리스트의 순서 값으로 반환
        if (listId.equals(previousListId) || listId.equals(nextListId) || previousListId.equals(nextListId)) {
            List targetList = listRepository.findById(listId)
                    .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
            log.info("같은 ID 감지 - listId: {}, previousListId: {}, nextListId: {}", listId, previousListId, nextListId);
            return new ListMoveResult.SingleListMove(new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }

        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
        log.info("타겟 리스트 조회 완료 - targetListId: {}, currentOrder: {}", targetList.getId(), targetList.getMyOrder());

        listService.checkBoardMember(targetList, member, MOVE_LIST);
        log.info("보드 접근 권한 확인 완료 - memberId: {}, boardId: {}", member.getId(), targetList.getBoard().getId());

        List previousList = listRepository.findById(previousListId)
                .orElseThrow(() -> new NotFoundEntityException(previousListId, "이전 리스트"));
        List nextList = listRepository.findById(nextListId)
                .orElseThrow(() -> new NotFoundEntityException(nextListId, "다음 리스트"));

        long prevOrder = previousList.getMyOrder();
        long nextOrder = nextList.getMyOrder();
        long gap = nextOrder - prevOrder;

        log.info("중간 위치 계산 - prevOrder: {}, nextOrder: {}, gap: {}", prevOrder, nextOrder, gap);

        long baseOrder = (gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP)
                ? prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
                : (prevOrder + nextOrder) / HALF_DIVIDER;
        log.info("기준 순서 값 설정 - baseOrder: {}", baseOrder);

        java.util.List<ListMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(targetList.getBoard(), baseOrder);
        log.info("고유 순서 값 생성 및 재배치 체크 완료 - orderInfoList size: {}", orderInfoList.size());

        if (orderInfoList.size() > 1) {
            log.info("재배치 필요 - 전체 리스트 반환");
            return new ListMoveResult.ReorderedListMove(orderInfoList);
        }

        targetList.setMyOrder(orderInfoList.getFirst().myOrder());
        listRepository.save(targetList);
        log.info("리스트 중간에 성공적으로 배치 - newOrder: {}", orderInfoList.getFirst().myOrder());

        return new ListMoveResult.SingleListMove(orderInfoList.getFirst());
    }


    // 고유성 보장을 위해 임의 간격 조정 로직 추가
    // 고유성 보장을 위해 임의 간격 조정 로직 추가
    private long generateUniqueOrder(long baseOrder) {
        long gap = LARGE_INCREMENT / 100; // LARGE_INCREMENT의 1%를 기본 간격으로 사용
        long offset = System.nanoTime() % gap;
        long uniqueOrder = baseOrder + offset;
        log.info("generateUniqueOrder - baseOrder: {}, gap: {}, offset: {}, uniqueOrder: {}", baseOrder, gap, offset, uniqueOrder);
        return uniqueOrder;
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
        boolean conflictExists = listRepository.existsByBoardAndMyOrder(board, order);
        log.info("isOrderConflict - boardId: {}, order: {}, conflictExists: {}", board.getId(), order, conflictExists);
        return conflictExists;
    }
}
