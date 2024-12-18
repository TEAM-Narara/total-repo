package com.narara.superboard.websocket.enums;

public enum BoardAction {
    //BoardMember
    ADD_BOARD_MEMBER,
    DELETE_BOARD_MEMBER,
    EDIT_BOARD_MEMBER,
    EDIT_BOARD_WATCH,

    //Card
    ADD_CARD,
    EDIT_CARD,
    ARCHIVE_CARD,
    DELETE_CARD,
    MOVE_CARD,

    //CardMember
    ADD_CARD_MEMBER,
    DELETE_CARD_MEMBER,
    EDIT_CARD_MEMBER,

    //Label
    ADD_BOARD_LABEL,
    EDIT_BOARD_LABEL,
    DELETE_BOARD_LABEL,
    EDIT_CARD_LABEL,

    //CardLabel
    ADD_CARD_LABEL,
    DELETE_CARD_LABEL,

    //List
    ADD_LIST,
    EDIT_LIST_ARCHIVE,
    EDIT_LIST,
    MOVE_LIST,

    //Reply
    ADD_REPLY,
    DELETE_REPLY,
    EDIT_REPLY,

    //Attachment
    ADD_CARD_ATTACHMENT,
    DELETE_CARD_ATTACHMENT,
    EDIT_CARD_ATTACHMENT_COVER //첨부파일 커버 상태 변경
}
