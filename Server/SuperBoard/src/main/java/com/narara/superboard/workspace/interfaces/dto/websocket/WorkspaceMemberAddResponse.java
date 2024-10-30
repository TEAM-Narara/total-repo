package com.narara.superboard.workspace.interfaces.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WorkspaceMemberAddResponse implements WebSocketData {
    private Long workspaceId;
    private Long workspaceOffset;
}
