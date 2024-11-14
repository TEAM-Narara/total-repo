package com.narara.superboard.board.document;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.document.Where;

public record BoardWhere(
        Long boardId,
        String boardName
) implements Where {
    public static BoardWhere of(Board board) {
        return new BoardWhere(board.getId(), board.getName());
    }

    @Override
    public Long cardId() {
        return null;
    }

    @Override
    public String cardName() {
        return null;
    }
}
