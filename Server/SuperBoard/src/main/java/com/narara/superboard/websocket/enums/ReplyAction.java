package com.narara.superboard.websocket.enums;

import com.narara.superboard.websocket.constant.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReplyAction implements Action {
    ADD_REPLY("댓글 생성"),
    EDIT_REPLY("댓글 수정"),
    DELETE_REPLY("댓글 삭제");

    private final String value;
}
