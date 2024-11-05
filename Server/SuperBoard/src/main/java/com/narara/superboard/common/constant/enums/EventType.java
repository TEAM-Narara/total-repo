package com.narara.superboard.common.constant.enums;

public enum EventType {
    CREATE,
    UPDATE,
    DELETE,
    MOVE,
    ARCHIVE,
    ADD,    // 멤버 추가
    REMOVE, // 멤버 해제
    GRANT,  // 권한 허용
    REVOKE  // 권한 해제
}