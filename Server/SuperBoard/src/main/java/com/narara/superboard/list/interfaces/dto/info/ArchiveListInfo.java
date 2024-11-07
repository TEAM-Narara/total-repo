package com.narara.superboard.list.interfaces.dto.info;

import com.narara.superboard.common.document.Target;

public record ArchiveListInfo(
        Long listId,
        String listName,
        boolean isArchived
) implements Target { }
