package com.narara.superboard.workspacemember.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import java.util.List;

public interface WorkSpaceMemberService {
    MemberCollectionResponseDto getWorkspaceMemberCollectionResponseDto(Long workspaceId);
    WorkSpaceListResponseDto getMemberWorkspaceList(Member member);

    WorkSpaceMember editAuthority(Long memberId, Long workspaceId, Authority authority);
    WorkSpaceMember addMember(Long workspaceId, Long memberId, Authority authority) throws FirebaseMessagingException;
    WorkSpaceMember deleteMember(Member member, Long workspaceId, Long memberId) throws FirebaseMessagingException;

    List<WorkSpaceMember> getWorkspaceMember(Long workspaceId);
}

