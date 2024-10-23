package com.narara.superboard.boardmember.service;

import com.narara.superboard.boardmember.interfaces.dto.BoardMemberCollectionResponseDto;

public interface BoardMemberService {
    BoardMemberCollectionResponseDto getBoardMemberCollectionResponseDto(Long boardId);
}
