package com.narara.superboard.list.interfaces.dto.info;



public record ArchiveListInfo(
        Long listId,
        String listName,
        boolean isArchived
) { }
