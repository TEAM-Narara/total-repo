package com.narara.superboard.board.interfaces.dto.log;


import com.narara.superboard.board.document.BoardInfo;

// Board 업데이트 관련 정보
public record UpdateBoardInfo(
        Long boardId,
        String boardName,
        String workspaceName
) implements BoardInfo { }
