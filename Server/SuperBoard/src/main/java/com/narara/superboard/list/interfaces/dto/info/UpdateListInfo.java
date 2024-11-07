package com.narara.superboard.list.interfaces.dto.info;

import com.narara.superboard.common.document.AdditionalDetails;

// 리스트 업데이트 관련 정보
public record UpdateListInfo(
        String listName
) implements AdditionalDetails { }
