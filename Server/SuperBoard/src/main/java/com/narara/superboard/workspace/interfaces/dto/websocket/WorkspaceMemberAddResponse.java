package com.narara.superboard.workspace.interfaces.dto.websocket;

import com.narara.superboard.common.constant.enums.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WorkspaceMemberAddResponse implements WebSocketData {
    private Long workspaceId;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String memberProfileImgUrl;
    private Authority authority;
    private Long workspaceOffset;
}
