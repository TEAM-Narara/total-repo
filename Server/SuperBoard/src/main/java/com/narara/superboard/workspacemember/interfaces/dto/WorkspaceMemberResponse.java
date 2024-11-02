package com.narara.superboard.workspacemember.interfaces.dto;

import com.narara.superboard.common.constant.enums.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WorkspaceMemberResponse {
    private Long memberId;
    private Long workspaceId;
    private String memberName;
    private String workspaceName;
    private Authority authority;
}
