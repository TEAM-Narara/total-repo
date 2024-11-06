package com.narara.superboard.board.interfaces.dto.log;

import com.narara.superboard.common.document.AdditionalDetails;

// Board 생성 관련 정보
public record CreateBoardInfo(
        String boardName,
        String workspaceName
) implements AdditionalDetails { }
