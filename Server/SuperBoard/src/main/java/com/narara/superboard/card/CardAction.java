package com.narara.superboard.card;

import com.narara.superboard.websocket.constant.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CardAction implements Action {
    ADD_CARD("카드 생성"),
    EDIT_CARD("카드 수정"),
    DELETE_CARD("카드 삭제"),
    ARCHIVE_CARD("카드 아카이브 여부 변경"),
    GET_ARCHIVE_CARD("카드 아카이브 조회"),
    MOVE_CARD("카드 이동");

    private final String value;
}
