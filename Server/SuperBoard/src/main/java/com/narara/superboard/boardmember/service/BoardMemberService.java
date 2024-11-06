package com.narara.superboard.boardmember.service;

import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;

public interface BoardMemberService {
    MemberCollectionResponseDto getBoardMemberCollectionResponseDto(Long boardId);
    Boolean getWatchStatus(Long boardId, Member member);
    void updateWatchStatus(Long boardId, Member member);
    BoardMember addBoardMember(Long boardId, Long inviteMemberId);
    BoardMember editBoardMemberAuthority(Long boardId, Long editMember, Authority authority);
    BoardMember deleteMember(Long boardId, Long deleteMemberId);
}
