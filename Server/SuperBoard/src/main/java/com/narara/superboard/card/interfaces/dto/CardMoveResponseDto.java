package com.narara.superboard.card.interfaces.dto;

import com.narara.superboard.card.entity.Card;

import java.util.ArrayList;
import java.util.List;

public record CardMoveResponseDto(Long cardId, Long movedListId, Long myOrder) {
    public static List<CardMoveResponseDto> of(List<Card> updatedCardCollection) {
        List<CardMoveResponseDto> cardMoveResponseDtos = new ArrayList<>();

        for (Card card : updatedCardCollection) {
            cardMoveResponseDtos.add(new CardMoveResponseDto(card.getId(), card.getList().getId(), card.getMyOrder()));
        }

        return cardMoveResponseDtos;
    }
}
