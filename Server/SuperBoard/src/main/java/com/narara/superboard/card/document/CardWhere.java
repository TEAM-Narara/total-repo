package com.narara.superboard.card.document;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.document.Where;

public record CardWhere(
        Long boardId,
        String boardName,
        Long cardId,
        String cardName
) implements Where {
    public static CardWhere of(Board board, Card card) {
        return new CardWhere(board.getId(), board.getName(), card.getId(), card.getName());
    }
}
