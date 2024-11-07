package com.narara.superboard.list.interfaces.dto.info;

import com.narara.superboard.common.document.Target;

// 리스트 업데이트 관련 정보
public record UpdateListInfo(
        Long listId,
        String listName
) implements Target { }
