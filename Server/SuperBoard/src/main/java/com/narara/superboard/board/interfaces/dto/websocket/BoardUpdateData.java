package com.narara.superboard.board.interfaces.dto.websocket;

import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BoardUpdateData implements WebSocketData {
    private Long boardId;
}
