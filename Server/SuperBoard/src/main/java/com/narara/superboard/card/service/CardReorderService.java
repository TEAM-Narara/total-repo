package com.narara.superboard.card.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.CardMoveResponseDto;
import com.narara.superboard.list.entity.List;


public interface CardReorderService {

    java.util.List<CardMoveResponseDto> reorderAllCardOrders(List list, Card targetCard, int targetIndex);
}
