package com.narara.superboard.list.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;

import com.narara.superboard.list.interfaces.dto.ListMoveCollectionRequest;
import com.narara.superboard.list.interfaces.dto.ListMoveRequest;
import com.narara.superboard.list.interfaces.dto.ListMoveResult.ReorderedListMove;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        if (targetList.getMyOrder().equals(topList.get().getMyOrder())) {
            log.info("이미 리스트가 최상위에 위치 - listId: {}", targetList.getId());
            return new ListMoveResult.SingleListMove(
                    new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }

        // 기준 순서 값 계산 (topList가 있을 경우 비율 기반 순서로, 없으면 기본 순서 값으로 설정)
        long baseOrder = topList.map(list -> {
            long maxLimit = Math.round(list.getMyOrder() * MOVE_TOP_ORDER_RATIO);
            long calculatedOrder = Math.max(list.getMyOrder() - LARGE_INCREMENT, maxLimit);
            log.info("기준 순서 값 계산 - topListOrder: {}, calculatedOrder: {}", list.getMyOrder(), calculatedOrder);
            return calculatedOrder;
        }).orElse(DEFAULT_TOP_ORDER);
        log.info("기준 순서 값 설정 - baseOrder: {}", baseOrder);

        java.util.List<ListMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(
                targetList, 0, board,
                baseOrder, null, topList.get().getMyOrder());
        log.info("고유 순서 값 생성 및 재배치 체크 완료 - orderInfoList size: {}", orderInfoList.size());

        if (orderInfoList.size() > 1) {
            log.info("재배치 필요 - 전체 리스트 반환");
            return new ListMoveResult.ReorderedListMove(orderInfoList);
        }

        targetList.setMyOrder(orderInfoList.getFirst().myOrder());

        log.info("리스트 최상위로 이동 완료 - newOrder: {}", orderInfoList.getFirst().myOrder());

        return new ListMoveResult.SingleListMove(orderInfoList.getFirst());
    }


    @Override
    @Transactional
    public ListMoveResult moveListToBottom(Member member, Long listId) {
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
            return new ListMoveResult.SingleListMove(
                    new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }

        // 현재 보드에서 가장 큰 순서 값을 가져오고, 없으면 기본값 사용
        long baseOrder = bottomList.map(lastList -> {
            long minLimit = lastList.getMyOrder() + Math.round(
                    (Long.MAX_VALUE - lastList.getMyOrder()) * MOVE_BOTTOM_ORDER_RATIO);
            long calculatedOrder = Math.min(lastList.getMyOrder() + LARGE_INCREMENT, minLimit);
            log.info("순서 값 계산 - lastListOrder: {}, calculatedOrder: {}", lastList.getMyOrder(), calculatedOrder);
            return calculatedOrder;
        }).orElse(DEFAULT_TOP_ORDER);

        log.info("기준 순서 값 설정 - baseOrder: {}", baseOrder);

        // 고유한 newOrder 값을 재시도로 생성 및 재배치 체크
        java.util.List<ListMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(
                targetList, -1, board,
                baseOrder, bottomList.get().getMyOrder(), null);
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
        log.info("moveListBetween 메서드 시작 - listId: {}, previousListId: {}, nextListId: {}", listId, previousListId,
                nextListId);

        // 동일한 ID가 있는지 확인하여, 동일한 경우 현재 리스트의 순서 값으로 반환
        if (listId.equals(previousListId) || listId.equals(nextListId) || previousListId.equals(nextListId)) {
            List targetList = listRepository.findById(listId)
                    .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
            log.info("같은 ID 감지 - listId: {}, previousListId: {}, nextListId: {}", listId, previousListId, nextListId);
            return new ListMoveResult.SingleListMove(
                    new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
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

        // 이전 이후의 리스트가 같은 보드에 있는가?
        if (!previousList.getBoard().getId().equals(nextList.getBoard().getId())) {
            log.info("이전 List와 이후 List가 다른 Board에 있습니다.");
            return new ListMoveResult.SingleListMove(
                    new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }

        // 보드의 전체 리스트를 myOrder 순서로 정렬하여 가져옴
        java.util.List<List> allLists = listRepository.findAllByBoardOrderByMyOrderAsc(targetList.getBoard());
        int previousIndex = allLists.indexOf(previousList);
        int nextIndex = allLists.indexOf(nextList);
        int targetIndex = previousIndex + 1;

        // 전체 순서에서 이전 리스트와 다음 리스트가 인접해 있는지 확인
        if (previousIndex != -1 && nextIndex != -1 && !(nextIndex == previousIndex + 1)) {
            log.info("이전 리스트와 다음 리스트가 전체 순서에서 바로 인접해 있지 않음. - previousListId: {}, nextListId: {}", previousListId, nextListId);
            return new ListMoveResult.SingleListMove(
                    new ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()));
        }

        long prevOrder = previousList.getMyOrder();
        long nextOrder = nextList.getMyOrder();
        long gap = nextOrder - prevOrder;

        log.info("중간 위치 계산 - prevOrder: {}, nextOrder: {}, gap: {}", prevOrder, nextOrder, gap);

        long baseOrder = (gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP)
                ? prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
                : (prevOrder + nextOrder) / HALF_DIVIDER;
        log.info("기준 순서 값 설정 - baseOrder: {}", baseOrder);


        java.util.List<ListMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(
                targetList, targetIndex, previousList.getBoard(),
                baseOrder, prevOrder, nextOrder);
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

    @Override
    @Transactional
    public ListMoveResult moveListVersion1(Member member, Long listId, Long myOrder) {
        //바꿀 리스트 조회
        List targetList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));

        //유저의 보드 접근 권한을 확인
        listService.checkBoardMember(targetList, member, MOVE_LIST);

        // 보드의 전체 리스트를 myOrder 순서로 정렬하여 가져옴 - 그냥 락 걸었음 TODO 락 범위관련 성능개선
        java.util.List<List> allLists = listRepository.findAllByBoardOrderByMyOrderAsc(targetList.getBoard());

        //리스트 myOrder 배정 및 재배치
        java.util.List<List> updatedListCollection = insertAndRelocate(allLists, targetList, myOrder);

        //혹시모를 에러를 위한 sort: 업데이트된 리스트들을 순서대로 정렬
        Collections.sort(updatedListCollection, Comparator.comparingLong(List::getMyOrder));

        //dto에 바뀐 리스트 값 매핑
        java.util.List<ListMoveResponseDto> orderInfoList = ListMoveResponseDto.of(updatedListCollection);

        return new ListMoveResult.ReorderedListMove(orderInfoList);
    }

    @Override
    @Transactional
    public ListMoveResult moveListVersion2(Member member, ListMoveCollectionRequest listMoveCollectionRequest) {
        java.util.List<ListMoveRequest> listMoveRequests = listMoveCollectionRequest.moveRequest();
        if (listMoveRequests.isEmpty()) {
            throw new IllegalArgumentException("잘못된 입력입니다");
        }

        //사이즈가 1이라면(하나만 이동한다면) version 1을 호출
        if (listMoveRequests.size() == 1) {
            return moveListVersion1(member, listMoveRequests.get(0).listId(), listMoveRequests.get(0).myOrder());
        }

        //유저의 보드 접근 권한을 확인
        List testList = listRepository.findById(listMoveRequests.get(0).listId())
                .orElseThrow(() -> new NotFoundEntityException(listMoveRequests.get(0).listId(), "리스트"));
        listService.checkBoardMember(testList, member, MOVE_LIST);

        // 보드의 전체 리스트를 myOrder 순서로 정렬하여 가져옴 - 그냥 락 걸었음 TODO 락 범위관련 성능개선
        java.util.List<List> allLists = listRepository.findAllByBoardOrderByMyOrderAsc(testList.getBoard());

        //하나의 보드에서 가져온 리스트들인지 검증
        validateOneBoard(allLists);

        //리스트 myOrder 배정 및 재배치
        java.util.List<List> updatedListCollection = insertAndRelocateList(allLists, listMoveRequests);

        return new ReorderedListMove(ListMoveResponseDto.of(updatedListCollection));
    }

    //모든 리스트들이 하나의 보드에 있는지 확인하기
    private void validateOneBoard(java.util.List<List> allLists) {
        if (allLists.isEmpty()) {
            throw new IllegalArgumentException("수정할 리스트는 비어있으면 안됩니다");
        }

        Board board = allLists.get(0).getBoard();
        for (List list: allLists) {
            if (!list.getBoard().getId().equals(board.getId())) {
                throw new IllegalArgumentException("입력으로 들어온 리스트들은 하나의 보드에 있어야합니다");
            }
        }
    }

    private java.util.List<List> insertAndRelocateList(java.util.List<List> allLists,
                                                       java.util.List<ListMoveRequest> listMoveRequests) {
        //request에 포함된 list들을 모두 제거해줌
        for (ListMoveRequest listMoveRequest : listMoveRequests) {
            for (List tmpList: allLists) {
                if (tmpList.getId().equals(listMoveRequest.listId())) {
                    allLists.remove(tmpList);
                    break;
                }
            }
        }

        java.util.List<List> updatedList = new ArrayList<>(); //업데이트된 리스트들을 저장함

        int idx = 0;
        while (idx < listMoveRequests.size()) {
            ListMoveRequest currentListRequest = listMoveRequests.get(idx);
            List currentList = listRepository.findById(currentListRequest.listId())
                    .orElseThrow(() -> new NotFoundEntityException(currentListRequest.listId(), "리스트"));

            int wantedIdx = findIdxInAllList(allLists, currentListRequest.myOrder());

            if (allLists.size() <= wantedIdx) {
                currentList.setMyOrder(currentListRequest.myOrder()); //업뎃
                updatedList.add(currentList);
                idx++;
                continue;
            }

            List alreadyExistsList = allLists.get(wantedIdx);

            //이미 wantedOrder를 MyOrder로 가지는 리스트가 있다면 listMoveRequests 업데이트
            if (alreadyExistsList.getMyOrder().equals(currentListRequest.myOrder())) {
                int gap = 1;
                long updateOrder = listMoveRequests.get(listMoveRequests.size() - 1).myOrder() + gap; //TODO 적당히 잘 띄우기
                listMoveRequests.add(new ListMoveRequest(alreadyExistsList.getId(), updateOrder));
            }

            currentList.setMyOrder(currentListRequest.myOrder()); //업뎃
            idx++;

            updatedList.add(currentList);
        }

        return updatedList;
    }

    private java.util.List<List> insertAndRelocate(java.util.List<List> allLists, List targetList, Long wantedOrder) {
        //1. allLists에 targetList가 이미 존재한다면 삭제(에러 방지를 위해)
        for (List existList: allLists) {
            if (existList.getId().equals(targetList.getId())) {
                allLists.remove(targetList);
                break;
            }
        }

        //이진 탐색으로 내가 원하는 위치를 찾아냄
        int wantedIdx = findIdxInAllList(allLists, wantedOrder);

        return updateMyOrder(allLists, wantedIdx, targetList, wantedOrder);
    }

    private java.util.List<List> updateMyOrder(java.util.List<List> allLists, int wantedIdx, List targetList, Long wantedOrder) {
        java.util.List<List> updatedList = new ArrayList<>();

        //맨 뒤라면 바로 업데이트
        if (allLists.size() <= wantedIdx) {
            targetList.setMyOrder(wantedOrder);
            return new ArrayList<>(java.util.List.of(targetList));
        }

        List currentList = allLists.get(wantedIdx);

        //이미 wantedOrder를 MyOrder로 가지는 리스트가 있다면 재귀 업데이트
        if (currentList.getMyOrder().equals(wantedOrder)) {
            java.util.List<List> nextUpdatedList = updateMyOrder(allLists, wantedIdx + 1, currentList, wantedOrder + 1);
            updatedList.addAll(nextUpdatedList);
        }

        targetList.setMyOrder(wantedOrder);
        updatedList.add(targetList);

        return updatedList;
    }

    private int findIdxInAllList(java.util.List<List> allLists, Long targetOrder) {
        if (allLists == null) {
            throw new IllegalArgumentException("allLists cannot be null");
        }

        //이진탐색을 통해 원하는 인덱스를 찾아냄
        int startIdx = 0;
        int endIdx = allLists.size();

        while (startIdx < endIdx) {
            int midIdx = startIdx + (endIdx - startIdx) / 2;
            List midList = allLists.get(midIdx);

            if (midList == null || midList.getMyOrder() == null) {
                throw new IllegalStateException("Invalid list or order at index: " + midIdx);
            }

            if (midList.getMyOrder() < targetOrder) {
                startIdx = midIdx + 1;
            } else {
                endIdx = midIdx;
            }
        }

        return startIdx;
    }

    private long generateUniqueOrder(long baseOrder, long maxOffset) {
        // 0부터 maxOffset까지의 범위에서 랜덤 offset 값을 생성
        long offset = ThreadLocalRandom.current().nextLong(0, maxOffset);
        long uniqueOrder = baseOrder + offset;

        // 로깅을 통해 순서 생성 과정 확인
        log.info("generateUniqueOrder - baseOrder: {}, maxOffset: {}, offset: {}, uniqueOrder: {}",
                baseOrder, maxOffset, offset, uniqueOrder);

        return uniqueOrder;
    }

    private java.util.List<ListMoveResponseDto> generateUniqueOrderWithRetry(List targetList, int targetIndex,
                                                                             Board board, long baseOrder,
                                                                             Long prevOrder, Long nextOrder) {
        int maxAttempts = 1;
        int attempt = 0;
        long maxOffset = 100;

        // 맨 위로 이동할 경우: offset이 기존 최상위 order보다 크지 않도록 제한
        if (prevOrder == null && nextOrder != null) {
            maxOffset = Math.min(maxOffset, nextOrder - baseOrder - 1);
        }
        // 두 리스트 사이에 배치할 경우: offset이 두 리스트의 중간값을 넘지 않도록 제한
        else if (prevOrder != null && nextOrder != null) {
            maxOffset = Math.min(maxOffset, (nextOrder - prevOrder) / 2);
        }
        if (maxOffset < 1) {
            maxOffset = 1;
        }
        log.info("maxOffset 생성  - maxOffset {}", maxOffset);

        long newOrder = generateUniqueOrder(baseOrder, maxOffset);

        while (attempt < maxAttempts) {
            log.info("고유 순서 생성 시도 - attempt: {}, newOrder: {}", attempt + 1, newOrder);

            // 순서 값이 유효 범위를 벗어나는 경우 재배치 수행
            if (newOrder <= 0 || newOrder >= Long.MAX_VALUE) {
                log.info("순서 값이 유효 범위를 벗어남 - 재배치 필요");
                return listReorderService.reorderAllListOrders(board, targetList, targetIndex);
            }

            if (!isOrderConflict(board, newOrder)) {
                log.info("순서 값 충돌 없음 - 고유 순서 값 반환: {}", newOrder);
                return java.util.List.of(new ListMoveResponseDto(targetList.getId(), newOrder));
            } else {
                log.info("순서 값 충돌 발생 - 새로운 순서 값 생성 시도");

                long randomOffset = ThreadLocalRandom.current().nextLong(0, maxOffset);
                newOrder = baseOrder + randomOffset;

                log.info("새로운 순서 값 계산 - newOrder: {}, baseOrder: {}, 시도 횟수 가중치: {}, randomOffset: {}",
                        newOrder, baseOrder, (attempt + 1) * 100L, randomOffset);
                attempt++;
            }
        }
        return listReorderService.reorderAllListOrders(board, targetList, targetIndex);
    }


    // 리스트 순서 중복 확인 메서드
    private boolean isOrderConflict(Board board, long order) {
        boolean conflictExists = listRepository.existsByBoardAndMyOrder(board, order);
        log.info("isOrderConflict - boardId: {}, order: {}, conflictExists: {}", board.getId(), order, conflictExists);
        return conflictExists;
    }
}
