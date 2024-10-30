package com.narara.superboard.websocket.interfaces.dto.websocket;

import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.common.constant.enums.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BoardAddMemberDto {
    private Long boardId;
    private Long boardOffset;
    private Authority authority;
    private Visibility visibility;
}
