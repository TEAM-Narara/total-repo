package com.narara.superboard.board.interfaces.dto.log;



// Board 생성 관련 정보
public record CreateBoardInfo(
        Long boardId,
        String boardName,
        String workspaceName
) { }
