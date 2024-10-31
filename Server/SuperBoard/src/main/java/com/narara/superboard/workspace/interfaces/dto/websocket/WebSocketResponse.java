package com.narara.superboard.workspace.interfaces.dto.websocket;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.websocket.constant.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class WebSocketResponse {
    private String target;
    private String action;
    private WebSocketData data;

    public static WebSocketResponse of(String target, Action action, WebSocketData data) {
        return WebSocketResponse.builder()
            .target(target)
            .action(action.toString())
            .data(data)
            .build();
    }
}
