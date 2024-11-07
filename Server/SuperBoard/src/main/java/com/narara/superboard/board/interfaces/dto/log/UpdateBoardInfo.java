package com.narara.superboard.board.interfaces.dto.log;

import com.narara.superboard.common.document.Target;

// Board 업데이트 관련 정보
public record UpdateBoardInfo(
        Long boardId,
        String boardName,
        String workspaceName
) implements Target { }
