package com.narara.superboard.boardmember.interfaces.dto.log;

import com.narara.superboard.board.document.BoardInfo;

public record AddBoardMemberInfo(
        Long memberId,
        String memberNickname,
        Long boardId,
        String boardName
) implements BoardInfo { }
