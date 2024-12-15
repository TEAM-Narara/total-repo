package com.narara.superboard.board.interfaces.dto.log;


import com.narara.superboard.board.document.BoardInfo;

// Board 생성 관련 정보
public record CreateBoardInfo(
        Long boardId,
        String boardName,
        String workspaceName
) implements BoardInfo { }
