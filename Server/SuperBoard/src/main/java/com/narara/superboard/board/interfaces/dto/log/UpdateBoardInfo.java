package com.narara.superboard.board.interfaces.dto.log;

import com.narara.superboard.common.document.AdditionalDetails;

// Board 업데이트 관련 정보
public record UpdateBoardInfo(
        String boardName,
        String workspaceName
) implements AdditionalDetails { }
