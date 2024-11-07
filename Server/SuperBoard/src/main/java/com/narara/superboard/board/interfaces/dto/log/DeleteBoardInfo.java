package com.narara.superboard.board.interfaces.dto.log;

import com.narara.superboard.common.document.AdditionalDetails;

// Board 삭제 관련 정보
public record DeleteBoardInfo(
        String boardName,
        String workspaceName
) implements AdditionalDetails { }
