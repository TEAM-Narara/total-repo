package com.narara.superboard.websocket.interfaces.dto.websocket;

import com.narara.superboard.board.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BoardAddMemberDto {
    private Long boardId;
    private Long boardOffset;
    private Visibility visibility;
}
