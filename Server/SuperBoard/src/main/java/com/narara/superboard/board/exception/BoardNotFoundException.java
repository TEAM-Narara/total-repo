package com.narara.superboard.board.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class BoardNotFoundException extends NotFoundException {
    public BoardNotFoundException(Long boardId) {
        super("ID가 " + boardId + "인 보드를 찾을 수 없습니다.");
    }
}
