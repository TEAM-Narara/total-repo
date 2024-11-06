package com.narara.superboard.list.interfaces.dto.info;

import com.narara.superboard.common.document.AdditionalDetails;

// 리스트 생성 관련 정보
public record CreateListInfo(
        String listName,
        Long boardId
) implements AdditionalDetails { }
