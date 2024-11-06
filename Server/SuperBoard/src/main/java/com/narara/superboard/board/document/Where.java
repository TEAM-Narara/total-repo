package com.narara.superboard.board.document;

import com.narara.superboard.board.entity.Board;

public record Where(
        Long boardId,
        String boardName
) {
    public static Where of(Board board) {
        return new Where(board.getId(), board.getName());
    }
}
