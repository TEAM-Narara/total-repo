package com.narara.superboard.workspacemember.interfaces.dto;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceNameHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WorkspaceMemberResponse implements WorkSpaceNameHolder {
    private Long memberId;
    private Long workspaceId;
    private String memberName;
    private String workspaceName;
    private Authority authority;

    @Override
    public String name() {
        return workspaceName;
    }
}
