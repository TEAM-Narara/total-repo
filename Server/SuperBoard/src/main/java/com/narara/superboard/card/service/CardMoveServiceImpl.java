package com.narara.superboard.card.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardMoveResponseDto;
import com.narara.superboard.card.interfaces.dto.CardMoveResult;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.narara.superboard.card.CardAction.MOVE_CARD;
import static com.narara.superboard.common.constant.MoveConst.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardMoveServiceImpl implements CardMoveService {

    private final CardRepository cardRepository;
    private final ListRepository listRepository;
    private final CardReorderService cardReorderService; // CardReorderService 주입
    private final CardService cardService; // CardService 주입


    @Override
    @Transactional
    public CardMoveResult moveCardToTop(Member member, Long cardId, Long targetListId) {
        // 이동할 카드 조회
        Card targetCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
        // 현재 사용자가 카드에 접근할 수 있는 권한이 있는지 확인
        cardService.checkBoardMember(targetCard, member, MOVE_CARD);

        // targetListId가 0L이면 현재 카드의 리스트를 사용
        List targetList = (targetListId == 0L) ? targetCard.getList() :
                listRepository.findById(targetListId)
                        .orElseThrow(() -> new NotFoundEntityException(targetListId, "목록"));

        Card topCard = cardRepository.findFirstByListOrderByMyOrderAsc(targetList)
                .orElseGet(() -> {
                    // targetList가 비어 있는 경우 삭제 처리
                    listRepository.delete(targetList);
                    log.warn("targetList 비어 있어 삭제되었습니다 - listId: {}", targetList.getId());
                    return null; // null 반환
                });

        if (topCard == null) {
            return new CardMoveResult.DeletedCardMove(targetList.getId());
        }

        // 처음과 같은 상황이면 그대로 반환
        if (targetCard.getMyOrder().equals(topCard.getMyOrder())
                && targetList.getId().equals(targetCard.getList().getId())) {
            return new CardMoveResult.SingleCardMove(
                    new CardMoveResponseDto(targetCard.getId(), targetList.getId(), targetCard.getMyOrder()));
        }

        // baseOrder 계산
        long baseOrder = Math.max(topCard.getMyOrder() - LARGE_INCREMENT,
                Math.round(topCard.getMyOrder() * MOVE_TOP_ORDER_RATIO));
        log.info("기준 순서 값 계산 - topListOrder: {}, calculatedOrder: {}", topCard.getMyOrder(), baseOrder);

        return getCardMoveResult(targetCard, 0, targetList, baseOrder, null, topCard.getMyOrder());
    }

    @Override
    @Transactional
    public CardMoveResult moveCardToBottom(Member member, Long cardId, Long targetListId) {
        // 이동할 카드 조회
        Card targetCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
        // 현재 사용자가 카드에 접근할 수 있는 권한이 있는지 확인
        cardService.checkBoardMember(targetCard, member, MOVE_CARD);

        // targetListId가 0L이면 현재 카드의 리스트를 사용
        List targetList = (targetListId == 0L) ? targetCard.getList() :
                listRepository.findById(targetListId)
                        .orElseThrow(() -> new NotFoundEntityException(targetListId, "목록"));

        // 대상 리스트에서 가장 아래에 위치한 카드 조회
        Card bottomCard = cardRepository.findFirstByListOrderByMyOrderDesc(targetList)
                .orElseGet(() -> {
                    // targetList가 비어 있는 경우 삭제 처리
                    listRepository.delete(targetList);
                    log.warn("targetList가 비어 있어 삭제되었습니다 - listId: {}", targetList.getId());
                    return null; // null 반환
                });

        if (bottomCard == null) {
            return new CardMoveResult.DeletedCardMove(targetList.getId());
        }


        if (targetCard.getMyOrder().equals(bottomCard.getMyOrder())
                && targetList.getId().equals(targetCard.getList().getId())) {
            return new CardMoveResult.SingleCardMove(
                    new CardMoveResponseDto(targetCard.getId(), targetList.getId(), targetCard.getMyOrder()));
        }

        // 맨 아래로 이동하기 위한 기준 순서값 설정 (비율을 사용하여 순서 계산)
        long baseOrder = Math.min(
                bottomCard.getMyOrder() + LARGE_INCREMENT,
                bottomCard.getMyOrder() + Math.round((Long.MAX_VALUE - bottomCard.getMyOrder()) * MOVE_BOTTOM_ORDER_RATIO)
        );

        log.info("기준 순서 값 계산 - bottomListOrder: {}, baseOrder: {}", bottomCard.getMyOrder(), baseOrder);

        return getCardMoveResult(targetCard, -1, targetList, baseOrder, bottomCard.getMyOrder(), null);
    }

    /**
     * 카드 이동 결과를 반환하는 메서드
     *
     * @param targetCard 이동할 카드
     * @param targetList 이동할 대상 리스트
     * @param baseOrder  새롭게 설정할 순서값의 기준
     * @return 카드 이동 결과 객체 (단일 이동 또는 재배치가 필요한 경우 전체 카드 정보)
     */
    private CardMoveResult getCardMoveResult(
            Card targetCard, int targetIndex, List targetList,
            long baseOrder, Long prevOrder, Long nextOrder) {
        // 고유한 순서값 생성 및 재배치 체크
        java.util.List<CardMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(
                targetCard, targetIndex, targetList,
                baseOrder, prevOrder, nextOrder);

        // 여러 카드를 재배치해야 하는 경우 재배치 결과 반환
        if (orderInfoList.size() > 1) {
            return new CardMoveResult.ReorderedCardMove(orderInfoList);
        }

        // 카드의 새로운 리스트와 순서 설정 후 저장
        targetCard.moveToListWithOrder(targetList, orderInfoList.getFirst().myOrder());
        cardRepository.save(targetCard);

        // 단일 카드 이동 결과 반환
        return new CardMoveResult.SingleCardMove(
                new CardMoveResponseDto(targetCard.getId(), targetList.getId(), targetCard.getMyOrder()));
    }

    @Override
    @Transactional
    public CardMoveResult moveCardBetween(Member member, Long cardId, Long previousCardId, Long nextCardId) {
        log.info("moveCardBetween 메서드 시작 - cardId: {}, previousCardId: {}, nextCardId: {}", cardId, previousCardId,
                nextCardId);

        // 동일한 ID가 있는지 확인하여, 동일한 경우 현재 카드의 순서 값으로 반환
        if (cardId.equals(previousCardId) || cardId.equals(nextCardId) || previousCardId.equals(nextCardId)) {
            Card targetCard = cardRepository.findById(cardId)
                    .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
            log.info("같은 ID 감지 - cardId: {}, previousCardId: {}, nextCardId: {}", cardId, previousCardId, nextCardId);
            return new CardMoveResult.SingleCardMove(
                    new CardMoveResponseDto(targetCard.getId(), targetCard.getList().getId(), targetCard.getMyOrder()));
        }

        Card targetCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
        cardService.checkBoardMember(targetCard, member, MOVE_CARD);

        // 이전 카드와 다음 카드 조회
        Card previousCard = cardRepository.findById(previousCardId)
                .orElseThrow(() -> new NotFoundEntityException(previousCardId, "이전 카드"));
        Card nextCard = cardRepository.findById(nextCardId)
                .orElseThrow(() -> new NotFoundEntityException(nextCardId, "다음 카드"));

        // 두 카드가 동일한 리스트에 있는지 확인
        validateCardsInSameList(previousCard, nextCard);

        // 이전 카드와 다음 카드의 순서값을 가져옴
        long prevOrder = previousCard.getMyOrder();
        long nextOrder = nextCard.getMyOrder();
        long gap = nextOrder - prevOrder;

        // 간격이 클 경우 고정된 간격값을 적용하고, 작을 경우 중간값 사용
        long baseOrder = (gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP)
                ? prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
                : (prevOrder + nextOrder) / HALF_DIVIDER;

        int targetIndex = cardRepository.findAllByListOrderByMyOrderAsc(previousCard.getList())
                .indexOf(previousCard) + 1;

        // 고유한 순서값 생성 후 재배치 필요 여부 체크
        java.util.List<CardMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(
                targetCard, targetIndex, previousCard.getList(),
                baseOrder, prevOrder, nextOrder);

        // 여러 카드 재배치가 필요한 경우 재배치 결과 반환
        if (orderInfoList.size() > 1) {
            return new CardMoveResult.ReorderedCardMove(orderInfoList);
        }

        // 타겟 카드에 새로운 리스트와 순서 설정 후 저장
        targetCard.moveToListWithOrder(previousCard.getList(), orderInfoList.getFirst().myOrder());
        cardRepository.save(targetCard);

        return new CardMoveResult.SingleCardMove(
                new CardMoveResponseDto(targetCard.getId(), previousCard.getList().getId(), targetCard.getMyOrder()));
    }

    private long generateUniqueOrder(long baseOrder, long maxOffset) {
        // 0부터 maxOffset까지의 범위에서 랜덤 offset 값을 생성
        long offset = ThreadLocalRandom.current().nextLong(0, maxOffset);
        return baseOrder + offset;
    }

    private java.util.List<CardMoveResponseDto> generateUniqueOrderWithRetry(
            Card targetCard, int targetIndex,
            List list, long baseOrder,
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

        long newOrder = generateUniqueOrder(baseOrder, maxOffset);

        while (attempt < maxAttempts) {
            if (newOrder <= 0 || newOrder >= Long.MAX_VALUE) {
                return cardReorderService.reorderAllCardOrders(list, targetCard, targetIndex);
            }

            if (!isOrderConflict(list, newOrder)) {
                return java.util.List.of(
                        new CardMoveResponseDto(targetCard.getId(), targetCard.getList().getId(), newOrder));
            } else {
                long randomOffset = ThreadLocalRandom.current().nextLong(0, maxOffset);
                newOrder = baseOrder + randomOffset;
                attempt++;
            }
        }
        return cardReorderService.reorderAllCardOrders(list, targetCard, targetIndex);
    }

    private boolean isOrderConflict(List list, long order) {
        return cardRepository.existsByListAndMyOrder(list, order);
    }

    private void validateCardsInSameList(Card previousCard, Card nextCard) {
        if (!previousCard.getList().getId().equals(nextCard.getList().getId())) {
            throw new IllegalArgumentException("이전 카드와 다음 카드는 동일한 리스트에 있어야 합니다.");
        }
    }
}
