package com.narara.superboard.workspacemember.interfaces.dto;

import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import java.util.ArrayList;
import java.util.List;

public record WorkspaceMemberListDto(List<WorkspaceMemberResponse> workspaceMemberList) {
    public static WorkspaceMemberListDto from(List<WorkSpaceMember> workSpaceMemberList) {
        List<WorkspaceMemberResponse> workspaceMembers = new ArrayList<>();
        for (WorkSpaceMember workSpaceMember : workSpaceMemberList) {
            workspaceMembers.add(
                    new WorkspaceMemberResponse(
                            workSpaceMember.getMember().getId(),
                            workSpaceMember.getWorkSpace().getId(),
                            workSpaceMember.getMember().getNickname(),
                            workSpaceMember.getWorkSpace().getName(),
                            workSpaceMember.getAuthority()
                    )
            );
        }

        return new WorkspaceMemberListDto(workspaceMembers);
    }
}
