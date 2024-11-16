package com.narara.superboard.card.service;

import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardMoveCollectionRequest;
import com.narara.superboard.card.interfaces.dto.CardMoveRequest;
import com.narara.superboard.card.interfaces.dto.CardMoveResponseDto;
import com.narara.superboard.card.interfaces.dto.CardMoveResult;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.list.service.ListService;
import com.narara.superboard.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

import static com.narara.superboard.card.CardAction.MOVE_CARD;
import static com.narara.superboard.common.constant.MoveConst.*;
import static com.narara.superboard.list.ListAction.MOVE_LIST;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardMoveServiceImpl implements CardMoveService {

    private final CardRepository cardRepository;
    private final ListRepository listRepository;
    private final CardReorderService cardReorderService; // CardReorderService 주입
    private final CardService cardService; // CardService 주입
    private final ListService listService;
    private final BoardOffsetService boardOffsetService;

    @Override
    @Transactional //websocket response 관련한 코드가 없으니 사용하지 말것!
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
                    return null; // null 반환
                });

        if (topCard == null) {
            targetCard.moveToListWithOrder(targetList, DEFAULT_TOP_ORDER);

            return new CardMoveResult.SingleCardMove(
                    new CardMoveResponseDto(targetCard.getId(), targetList.getId(), targetCard.getMyOrder()));
        }

        if (topCard.getMyOrder() < 1) {
            // 의도적으로 재시도 로직 실행
            java.util.List<CardMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(
                    targetCard, 0, targetList,
                    -1000, null, topCard.getMyOrder());

            return new CardMoveResult.ReorderedCardMove(orderInfoList);
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

        java.util.List<CardMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(
                targetCard, 0, targetList,
                baseOrder, null, topCard.getMyOrder());

        // 여러 카드를 재배치해야 하는 경우 재배치 결과 반환
        if (orderInfoList.size() > 1) {
            return new CardMoveResult.ReorderedCardMove(orderInfoList);
        }

        // 카드의 새로운 리스트와 순서 설정 후 저장
        targetCard.moveToListWithOrder(targetList, orderInfoList.getFirst().myOrder());

        // 단일 카드 이동 결과 반환
        return new CardMoveResult.SingleCardMove(
                new CardMoveResponseDto(targetCard.getId(), targetList.getId(), orderInfoList.getFirst().myOrder()));
    }

    @Override
    @Transactional //websocket response 관련한 코드가 없으니 사용하지 말것!
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
                    return null; // null 반환
                });
        if (bottomCard == null) {
            targetCard.moveToListWithOrder(targetList, DEFAULT_TOP_ORDER);
            return new CardMoveResult.SingleCardMove(
                    new CardMoveResponseDto(targetCard.getId(), targetList.getId(), targetCard.getMyOrder()));
        }

        if (bottomCard.getMyOrder() >= 9223372036854775807L) {
            // 의도적으로 재시도 로직 실행
            java.util.List<CardMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(
                    targetCard, -1, targetList,
                    -1000, bottomCard.getMyOrder(), null);

            return new CardMoveResult.ReorderedCardMove(orderInfoList);
        }

        if (targetCard.getMyOrder().equals(bottomCard.getMyOrder())
                && targetList.getId().equals(targetCard.getList().getId())) {
            return new CardMoveResult.SingleCardMove(
                    new CardMoveResponseDto(targetCard.getId(), targetList.getId(), targetCard.getMyOrder()));
        }

        // 맨 아래로 이동하기 위한 기준 순서값 설정 (비율을 사용하여 순서 계산)
        long baseOrder = Math.min(
                bottomCard.getMyOrder() + LARGE_INCREMENT,
                bottomCard.getMyOrder() + Math.round(
                        (Long.MAX_VALUE - bottomCard.getMyOrder()) * MOVE_BOTTOM_ORDER_RATIO)
        );

        log.info("기준 순서 값 계산 - bottomListOrder: {}, baseOrder: {}", bottomCard.getMyOrder(), baseOrder);

        java.util.List<CardMoveResponseDto> orderInfoList = generateUniqueOrderWithRetry(
                targetCard, -1, targetList,
                baseOrder, bottomCard.getMyOrder(), null);

        // 여러 카드를 재배치해야 하는 경우 재배치 결과 반환
        if (orderInfoList.size() > 1) {
            return new CardMoveResult.ReorderedCardMove(orderInfoList);
        }

        // 카드의 새로운 리스트와 순서 설정 후 저장
        targetCard.moveToListWithOrder(targetList, orderInfoList.getFirst().myOrder());
        targetList.setLastCardOrder(orderInfoList.getFirst().myOrder());

        // 단일 카드 이동 결과 반환
        return new CardMoveResult.SingleCardMove(
                new CardMoveResponseDto(targetCard.getId(), targetList.getId(), orderInfoList.getFirst().myOrder()));
    }

    @Override
    @Transactional //websocket response 관련한 코드가 없으니 사용하지 말것!
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

        // 이전 이후의 리스트가 붙어있는 건가
        if (!previousCard.getList().getId().equals(nextCard.getList().getId())) {
            log.info("이전 Card와 이후 Card가 다른 List에 있습니다.");
            return new CardMoveResult.SingleCardMove(
                    new CardMoveResponseDto(targetCard.getId(), targetCard.getList().getId(), targetCard.getMyOrder()));
        }

        // 이전 카드와 다음 카드의 순서값을 가져옴
        long prevOrder = previousCard.getMyOrder();
        long nextOrder = nextCard.getMyOrder();
        long gap = nextOrder - prevOrder;

        // 간격이 클 경우 고정된 간격값을 적용하고, 작을 경우 중간값 사용
        long baseOrder = (gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP)
                ? prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
                : (prevOrder + nextOrder) / HALF_DIVIDER;

        java.util.List<Card> allLists = cardRepository.findAllByListOrderByMyOrderAsc(targetCard.getList());
        int previousIndex = allLists.indexOf(previousCard);
        int nextIndex = allLists.indexOf(nextCard);
        int targetIndex = previousIndex + 1;

        // 전체 순서에서 이전 리스트와 다음 리스트가 인접해 있는지 확인
        if (previousIndex != -1 && nextIndex != -1 && !(nextIndex == previousIndex + 1)) {
            log.info("이전 리스트와 다음 리스트가 전체 순서에서 바로 인접해 있지 않음. - previousCardId: {}, nextCardId: {}", previousCardId, nextCardId);
            return new CardMoveResult.SingleCardMove(
                    new CardMoveResponseDto(targetCard.getId(), targetCard.getList().getId(), targetCard.getMyOrder()));
        }


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
                new CardMoveResponseDto(targetCard.getId(), previousCard.getList().getId(), orderInfoList.getFirst().myOrder()));
    }

    @Override
    @Transactional
    public CardMoveResult moveCardVersion2(Member member, Long listId,
                                           CardMoveCollectionRequest cardMoveCollectionRequest) {
        //현재 리스트 조회
        List currentList = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));

        java.util.List<CardMoveRequest> cardMoveRequests = cardMoveCollectionRequest.moveRequest();
        if (cardMoveRequests.isEmpty()) {
            throw new IllegalArgumentException("빈 입력을 보내면 안됩니다!");
        }

        //사이즈가 1이라면(하나만 이동한다면) version 1을 호출
        if (cardMoveRequests.size() == 1) {
            return moveCardVersion1(member, currentList, cardMoveRequests.get(0).cardId(), cardMoveRequests.get(0).myOrder());
        }

        //유저의 보드 접근 권한을 확인
        listService.checkBoardMember(currentList, member, MOVE_LIST);

        //리스트의 전체 카드를 myOrder 순서로 정렬하여 가져옴 - 그냥 락 걸었음 TODO 락 범위관련 성능개선
        //request로 들어온 애들이 모두 같은 보드에 있는지 검증해야하나?
        java.util.List<Card> allCards = cardRepository.findAllByListOrderByMyOrderAsc(currentList);

        //카드 myOrder 배정 및 재배치
        java.util.List<Card> updatedCardCollection = insertAndRelocateCard(allCards, currentList, cardMoveRequests);

        if (!updatedCardCollection.isEmpty()) {
            //Websocket 카드 이동 response 보내기
            boardOffsetService.saveMoveCardDiff(updatedCardCollection, currentList.getBoard().getId());
        }

        //카드를 완전히 다른 리스트로 옮길 때만 알림이 옴
//        String title = String.format(
//                "%s moved the card [카드이름] to [리스트이름] on [보드이름] + [사용자 프로필사진]",
//                member.getNickname(),
//
//        );
//        fcmTokenService.sendMessage(member, title, "");

        return new CardMoveResult.ReorderedCardMove(CardMoveResponseDto.of(updatedCardCollection));
    }

    @Transactional
    public CardMoveResult moveCardVersion1(Member member, List targetList, Long cardId, Long myOrder) {
        //바꿀 리스트 조회
        Card targetCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "리스트"));

//        boolean isChangeList = !(targetCard.getList().getId().equals(targetList.getId()));

        //유저의 보드 접근 권한을 확인
        cardService.checkBoardMember(targetCard, member, MOVE_LIST);

        boolean isMoveAnotherList = !(targetCard.getList().getId().equals(targetList.getId()));

        // 보드의 전체 카드를 myOrder 순서로 정렬하여 가져옴 - 그냥 락 걸었음 TODO 락 범위관련 성능개선
        java.util.List<Card> allCards = cardRepository.findAllByListOrderByMyOrderAsc(targetList);

        //카드 myOrder 배정 및 재배치
        java.util.List<Card> updatedCardCollection = insertAndRelocate(allCards, targetList, targetCard, myOrder);

        //혹시모를 에러를 위한 sort: 업데이트된 카드들을 순서대로 정렬
        Collections.sort(updatedCardCollection, Comparator.comparingLong(Card::getMyOrder));

        //dto에 바뀐 리스트 값 매핑
        java.util.List<CardMoveResponseDto> orderInfoCard = CardMoveResponseDto.of(updatedCardCollection);

        if (!updatedCardCollection.isEmpty()) {
            //Websocket 카드 이동 response 보내기
            boardOffsetService.saveMoveCardDiff(updatedCardCollection, targetCard.getList().getBoard().getId());
        }

        //알림
//        if (isMoveAnotherList) {
//            //카드를 완전히 다른 리스트로 옮길 때만 알림이 옴
//            String title = String.format(
//                    "%s moved the card %s to %s on %s + [사용자 프로필사진]",
//                    member.getNickname(),
//                    targetCard.getName(),
//                    targetCard.getList().getName(),
//                    targetCard.getList().getBoard().getName()
//            );
//
//            fcmTokenService.sendMessage(member, title, "");
//        }

        return new CardMoveResult.ReorderedCardMove(orderInfoCard);
    }

    private java.util.List<Card> insertAndRelocateCard(java.util.List<Card> allCards,
                                                       List targetList,
                                                       java.util.List<CardMoveRequest> cardMoveRequests) {
        //request에 포함된 card들을 모두 제거해줌
        for (CardMoveRequest cardMoveRequest : cardMoveRequests) {
            for (Card tmpCard: allCards) {
                if (tmpCard.getId().equals(cardMoveRequest.cardId())) {
                    allCards.remove(tmpCard);
                    break;
                }
            }
        }

        int idx = 0;

        java.util.List<Card> updatedCard = new ArrayList<>();

        while (idx < cardMoveRequests.size()) {
            CardMoveRequest currentCardRequest = cardMoveRequests.get(idx);
            Card currentCard = cardRepository.findById(currentCardRequest.cardId())
                    .orElseThrow(() -> new NotFoundEntityException(currentCardRequest.cardId(), "리스트"));

            int wantedIdx = findIdxInAllCard(allCards, currentCardRequest.myOrder());

            if (allCards.size() <= wantedIdx) {
                //cardUpdate 로직
                updateCard(targetList, currentCard, currentCardRequest.myOrder());
                updatedCard.add(currentCard);
                log.info("@@@@ currentCard: " + currentCard.getId() + "      " + currentCard.getList().getId());
                idx++;
                continue;
            }

            Card alreadyExistsCard = allCards.get(wantedIdx);

            //이미 wantedOrder를 MyOrder로 가지는 리스트가 있다면 재귀 업데이트
            if (alreadyExistsCard.getMyOrder().equals(currentCardRequest.myOrder())) {
                int gap = 1;
                long updateOrder = cardMoveRequests.get(cardMoveRequests.size() - 1).myOrder() + gap; //TODO 적당히 잘 띄우기
                cardMoveRequests.add(new CardMoveRequest(alreadyExistsCard.getId(), updateOrder));
            }

            //cardUpdate 로직
            updateCard(targetList, currentCard, currentCardRequest.myOrder());
            log.info("@@@@ currentCard: " + currentCard.getId() + "      " + currentCard.getList().getId());
            idx++;

            updatedCard.add(currentCard);
        }

        return updatedCard;
    }

    private java.util.List<Card> insertAndRelocate(java.util.List<Card> allCards, List targetList, Card targetCard, Long wantedOrder) {
        //1. allLists에 targetList가 이미 존재한다면 삭제(에러 방지를 위해)
        for (Card existList: allCards) {
            if (existList.getId().equals(targetCard.getId())) {
                allCards.remove(targetCard);
                break;
            }
        }

        //이진 탐색으로 내가 원하는 위치를 찾아냄
        int wantedIdx = findIdxInAllCard(allCards, wantedOrder);

        return updateMyOrder(allCards, targetList, wantedIdx, targetCard, wantedOrder);
    }

    private java.util.List<Card> updateMyOrder(java.util.List<Card> allCards, List targetList, int wantedIdx, Card targetCard, Long wantedOrder) {
        java.util.List<Card> updatedCard = new ArrayList<>();

        //맨 뒤라면 바로 업데이트
        if (allCards.size() <= wantedIdx) {
            updateCard(targetList, targetCard, wantedOrder);
            return new ArrayList<>(java.util.List.of(targetCard));
        }

        Card currentCard = allCards.get(wantedIdx);

        //이미 wantedOrder를 MyOrder로 가지는 리스트가 있다면 재귀 업데이트
        if (currentCard.getMyOrder().equals(wantedOrder)) {
            java.util.List<Card> nextUpdatedList = updateMyOrder(allCards,  targetList, wantedIdx + 1, currentCard, wantedOrder + 1);
            updatedCard.addAll(nextUpdatedList);
        }

        updateCard(targetList, targetCard, wantedOrder);
        updatedCard.add(targetCard);

        return updatedCard;
    }

    private static void updateCard(List targetList, Card targetCard, Long wantedOrder) {
        targetCard.setMyOrder(wantedOrder);
        targetCard.updateList(targetList);
    }

    private int findIdxInAllCard(java.util.List<Card> allCards, Long targetOrder) {
        if (allCards == null) {
            throw new IllegalArgumentException("allLists cannot be null");
        }

        //이진탐색을 통해 원하는 인덱스를 찾아냄
        int startIdx = 0;
        int endIdx = allCards.size();

        while (startIdx < endIdx) {
            int midIdx = startIdx + (endIdx - startIdx) / 2;
            Card midCard = allCards.get(midIdx);

            if (midCard == null || midCard.getMyOrder() == null) {
                throw new IllegalStateException("Invalid list or order at index: " + midIdx);
            }

            if (midCard.getMyOrder() < targetOrder) {
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
