package com.narara.superboard.boardmember.interfaces.dto.log;

public record AddBoardMemberInfo(
        Long memberId,
        String memberNickname,
        Long boardId,
        String boardName
) { }