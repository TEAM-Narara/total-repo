package com.narara.superboard.workspace.interfaces.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WorkspaceCreateData implements WebSocketData {
    private Long workspaceId;
    private String name;
}
