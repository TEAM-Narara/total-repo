package com.narara.superboard.list.interfaces.dto.info;


import com.narara.superboard.board.document.BoardInfo;

// 리스트 생성 관련 정보
public record CreateListInfo(
        Long listId,
        String listName,
        Long boardId
) implements BoardInfo {
}
