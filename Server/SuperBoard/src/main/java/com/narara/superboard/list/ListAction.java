package com.narara.superboard.list;

import com.narara.superboard.websocket.constant.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ListAction implements Action {
    ADD_LIST("리스트 생성"),
    EDIT_LIST("리스트 수정"),
    DELETE_LIST("리스트 삭제"),
    ARCHIVE_LIST("리스트 아카이브 여부 변경");

    private final String value;
}
