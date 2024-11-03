package com.narara.superboard.workspacemember.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;

public interface WorkSpaceMemberService {

    WorkspaceMemberCollectionResponseDto getWorkspaceMemberCollectionResponseDto(Long workSpaceId);
    WorkSpaceListResponseDto getMemberWorkspaceList(Member member);

}
