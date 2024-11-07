package com.narara.superboard.board.interfaces.dto.log;

import com.narara.superboard.common.document.Target;

// Board 삭제 관련 정보
public record DeleteBoardInfo(
        Long boardId,
        String boardName,
        String workspaceName
) implements Target { }
