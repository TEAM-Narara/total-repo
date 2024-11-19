package com.narara.superboard.boardmember.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberResponseDto;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;

public interface BoardMemberService {
    BoardMemberResponseDto getBoardMemberCollectionResponseDto(Long boardId);
    Boolean getWatchStatus(Long boardId, Member member);
    void updateWatchStatus(Long boardId, Member member);
    BoardMember addBoardMember(Member member, Long boardId, Long inviteMemberId) throws FirebaseMessagingException;
    BoardMember editBoardMemberAuthority(Long boardId, Long editMember, Authority authority);
    BoardMember deleteMember(Member manOfAction, Long boardId, Long deleteMemberId) throws FirebaseMessagingException;
}
