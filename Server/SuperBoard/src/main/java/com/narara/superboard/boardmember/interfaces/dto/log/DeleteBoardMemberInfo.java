package com.narara.superboard.boardmember.interfaces.dto.log;

public record DeleteBoardMemberInfo(
        Long memberId,
        String memberNickname,
        Long boardId,
        String boardName
) { }