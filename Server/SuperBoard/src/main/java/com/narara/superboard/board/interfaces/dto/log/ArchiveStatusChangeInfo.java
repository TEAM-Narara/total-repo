package com.narara.superboard.board.interfaces.dto.log;

import com.narara.superboard.board.document.BoardInfo;

// Board 아카이브 상태 변경 관련 정보
public record ArchiveStatusChangeInfo(
        Long boardId,
        String boardName,
        boolean isArchived
) implements BoardInfo { }
