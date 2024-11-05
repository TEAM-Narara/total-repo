package com.narara.superboard.card.document;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;

public record Where(
        Long boardId,
        String boardName,
        Long cardId,
        String cardName
) {
    public static Where of(Board board, Card card) {
        return new Where(board.getId(), board.getName(), card.getId(), card.getName());
    }
}
