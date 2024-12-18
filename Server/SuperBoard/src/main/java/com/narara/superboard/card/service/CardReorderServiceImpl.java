package com.narara.superboard.card.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardMoveResponseDto;
import com.narara.superboard.common.constant.MoveConst;
import com.narara.superboard.list.entity.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static com.narara.superboard.common.constant.MoveConst.*;

@Service
@RequiredArgsConstructor
public class CardReorderServiceImpl implements CardReorderService {

    private final CardRepository cardRepository;

    @Override
    @Transactional
    public java.util.List<CardMoveResponseDto> reorderAllCardOrders(List list, Card targetCard, int targetIndex) {
        targetCard.moveToList(list);

        // 해당 보드의 모든 카드를 myOrder 기준으로 오름차순 정렬하여 조회
        java.util.List<Card> cards = cardRepository.findAllByListOrderByMyOrderAsc(list);

        // targetIndex -1일 경우 가장 아래로 삽입
        if (targetIndex == -1) {
            cards.remove(targetCard);
            cards.add(targetCard);  // 가장 마지막에 삽입
        } else {
            cards.remove(targetCard);
            cards.add(targetIndex, targetCard);  // 특정 위치에 삽입
        }

        // 초기 값 설정 및 결과 리스트 생성
        long newOrder = MoveConst.DEFAULT_TOP_ORDER;
        java.util.List<CardMoveResponseDto> orderInfoList = new ArrayList<>();
        for (Card card : cards) {
            card.setMyOrder(newOrder);
            orderInfoList.add(new CardMoveResponseDto(card.getId(), list.getId(), newOrder));
            newOrder += REORDER_GAP;
        }

        // 업데이트된 카드 순서 저장
        cardRepository.saveAll(cards);
        list.setLastCardOrder(newOrder - REORDER_GAP);

        return orderInfoList;
    }
}
