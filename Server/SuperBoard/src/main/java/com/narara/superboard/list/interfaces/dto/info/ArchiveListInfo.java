package com.narara.superboard.list.interfaces.dto.info;


import com.narara.superboard.board.document.BoardInfo;

public record ArchiveListInfo(
        Long listId,
        String listName,
        boolean isArchived
) implements BoardInfo { }
