package com.narara.superboard.boardmember.service;

import com.narara.superboard.boardmember.interfaces.dto.BoardMemberCollectionResponseDto;
import com.narara.superboard.member.entity.Member;

public interface BoardMemberService {
    BoardMemberCollectionResponseDto getBoardMemberCollectionResponseDto(Long boardId);
    Boolean getWatchStatus(Long boardId, Member member);
    void updateWatchStatus(Long boardId, Member member);
}
