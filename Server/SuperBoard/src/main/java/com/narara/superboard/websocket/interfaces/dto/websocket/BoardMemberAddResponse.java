package com.narara.superboard.websocket.interfaces.dto.websocket;

import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BoardMemberAddResponse implements WebSocketData {
    private Long boardId;
    private Long boardOffset;
}
