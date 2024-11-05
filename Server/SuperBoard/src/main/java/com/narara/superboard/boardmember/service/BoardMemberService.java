package com.narara.superboard.boardmember.service;

import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.member.entity.Member;

public interface BoardMemberService {
    MemberCollectionResponseDto getBoardMemberCollectionResponseDto(Long boardId);
    Boolean getWatchStatus(Long boardId, Member member);
    void updateWatchStatus(Long boardId, Member member);
}
