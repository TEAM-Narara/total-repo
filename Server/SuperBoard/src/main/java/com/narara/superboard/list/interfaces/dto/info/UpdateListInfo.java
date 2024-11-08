package com.narara.superboard.list.interfaces.dto.info;



// 리스트 업데이트 관련 정보
public record UpdateListInfo(
        Long listId,
        String listName
) { }
