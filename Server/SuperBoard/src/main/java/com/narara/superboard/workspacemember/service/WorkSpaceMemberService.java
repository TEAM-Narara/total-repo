package com.narara.superboard.workspacemember.service;

<<<<<<< Server/SuperBoard/src/main/java/com/narara/superboard/workspacemember/service/WorkSpaceMemberService.java
import com.narara.superboard.member.entity.Member;
=======
import com.narara.superboard.common.constant.enums.Authority;
>>>>>>> Server/SuperBoard/src/main/java/com/narara/superboard/workspacemember/service/WorkSpaceMemberService.java
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;

public interface WorkSpaceMemberService {
    WorkspaceMemberCollectionResponseDto getWorkspaceMemberCollectionResponseDto(Long workSpaceId);
    WorkSpaceListResponseDto getMemberWorkspaceList(Member member);

    WorkSpaceMember editAuthority(Long memberId, Long workspaceId, Authority authority);
    WorkSpaceMember addMember(Long workspaceId, Long memberId, Authority authority);
    WorkSpaceMember deleteMember(Long workspaceId, Long memberId);
}
