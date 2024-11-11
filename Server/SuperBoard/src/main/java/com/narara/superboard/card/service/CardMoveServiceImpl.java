package com.narara.superboard.card.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.CardMoveResponseDto;
import com.narara.superboard.card.interfaces.dto.CardMoveResult;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.narara.superboard.card.CardAction.MOVE_CARD;
import static com.narara.superboard.common.constant.MoveConst.*;

@Service
@RequiredArgsConstructor
public class CardMoveServiceImpl implements CardMoveService {

    private final com.narara.superboard.card.infrastructure.CardRepository cardRepository;
    private final CardReorderService cardReorderService; // CardReorderService 주입
    private final CardService cardService; // CardService 주입

    @Override
    @Transactional
    public CardMoveResult moveCardToTop(com.narara.superboard.member.entity.Member member, Long cardId) {
        Card targetCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new com.narara.superboard.common.exception.NotFoundEntityException(cardId, "카드"));
        cardService.checkBoardMember(targetCard, member, MOVE_CARD);

        List list = targetCard.getList();
        Optional<Card> topCard = cardRepository.findFirstByListOrderByMyOrderAsc(list);

        if (topCard.isPresent() && targetCard.getMyOrder().equals(topCard.get().getMyOrder())) {
            return new CardMoveResult.SingleCardMove(new CardMoveResponseDto(targetCard.getId(), targetCard.getMyOrder()));
        }

        long baseOrder = topCard.map(card -> Math.round(card.getMyOrder() * MOVE_TOP_ORDER_RATIO))
                .orElse(DEFAULT_TOP_ORDER);

        java.util.List<CardMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(list, baseOrder);

        if (orderInfoList.size() > 1) {
            return new CardMoveResult.ReorderedCardMove(orderInfoList);
        }

        targetCard.setMyOrder(orderInfoList.getFirst().myOrder());
        if (topCard.isEmpty()) {
            list.setLastCardOrder(orderInfoList.getFirst().myOrder());
        }

        return new CardMoveResult.SingleCardMove(orderInfoList.getFirst());
    }

    @Override
    @Transactional
    public CardMoveResult moveCardToBottom(Member member, Long cardId) {
        Card targetCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
        cardService.checkBoardMember(targetCard, member, MOVE_CARD);

        List List = targetCard.getList();
        Optional<Card> bottomCard = cardRepository.findFirstByListOrderByMyOrderDesc(List);

        if (bottomCard.isPresent() && targetCard.getMyOrder().equals(bottomCard.get().getMyOrder())) {
            return new CardMoveResult.SingleCardMove(new CardMoveResponseDto(targetCard.getId(), targetCard.getMyOrder()));
        }

        long baseOrder = bottomCard.map(lastCard -> {
            long maxLimit = lastCard.getMyOrder() + Math.round((Long.MAX_VALUE - lastCard.getMyOrder()) * MOVE_BOTTOM_ORDER_RATIO);
            return Math.min(lastCard.getMyOrder() + LARGE_INCREMENT, maxLimit);
        }).orElse(DEFAULT_TOP_ORDER);

        java.util.List<CardMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(List, baseOrder);

        if (orderInfoList.size() > 1) {
            return new CardMoveResult.ReorderedCardMove(orderInfoList);
        }

        targetCard.setMyOrder(orderInfoList.getFirst().myOrder());
        List.setLastCardOrder(orderInfoList.getFirst().myOrder());

        return new CardMoveResult.SingleCardMove(orderInfoList.getFirst());
    }

    @Override
    @Transactional
    public CardMoveResult moveCardBetween(Member member, Long cardId, Long previousCardId, Long nextCardId) {
        Card targetCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
        cardService.checkBoardMember(targetCard, member, MOVE_CARD);

        Card previousCard = cardRepository.findById(previousCardId)
                .orElseThrow(() -> new NotFoundEntityException(previousCardId, "이전 카드"));
        Card nextCard = cardRepository.findById(nextCardId)
                .orElseThrow(() -> new NotFoundEntityException(nextCardId, "다음 카드"));

        long prevOrder = previousCard.getMyOrder();
        long nextOrder = nextCard.getMyOrder();
        long gap = nextOrder - prevOrder;

        if (targetCard.getMyOrder() > prevOrder && targetCard.getMyOrder() < nextOrder) {
            return new CardMoveResult.SingleCardMove(new CardMoveResponseDto(targetCard.getId(), targetCard.getMyOrder()));
        }

        long baseOrder = (gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP)
                ? prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
                : (prevOrder + nextOrder) / HALF_DIVIDER;

        java.util.List<CardMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(targetCard.getList(), baseOrder);

        if (orderInfoList.size() > 1) {
            return new CardMoveResult.ReorderedCardMove(orderInfoList);
        }

        targetCard.setMyOrder(orderInfoList.getFirst().myOrder());
        cardRepository.save(targetCard);

        return new CardMoveResult.SingleCardMove(orderInfoList.getFirst());
    }

    private long generateUniqueOrder(long baseOrder) {
        long gap = LARGE_INCREMENT / 100;
        long offset = System.nanoTime() % gap;
        return baseOrder + offset;
    }

    private java.util.List<CardMoveResponseDto> generateUniqueOrderWithRetry(List List, long baseOrder) {
        int maxAttempts = 3;
        int attempt = 0;
        long newOrder = generateUniqueOrder(baseOrder);

        while (attempt < maxAttempts) {
            if (newOrder <= 0 || newOrder >= Long.MAX_VALUE) {
                return cardReorderService.reorderAllCardOrders(List);
            }

            if (!isOrderConflict(List, newOrder)) {
                return java.util.List.of(new CardMoveResponseDto(List.getId(), newOrder));
            } else {
                long randomOffset = ThreadLocalRandom.current().nextLong(50, 150);
                newOrder = baseOrder + (attempt + 1) * 100L + randomOffset;
                attempt++;
            }
        }
        throw new RuntimeException("최대 " + maxAttempts + "번의 시도 후에도 고유한 순서 값을 설정할 수 없습니다.");
    }

    private boolean isOrderConflict(List List, long order) {
        return cardRepository.existsByListAndMyOrder(List, order);
    }
}
