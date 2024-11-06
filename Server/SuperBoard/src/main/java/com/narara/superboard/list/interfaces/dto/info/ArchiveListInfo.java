package com.narara.superboard.list.interfaces.dto.info;

import com.narara.superboard.common.document.AdditionalDetails;

public record ArchiveListInfo(
        String listName,
        boolean isArchived
) implements AdditionalDetails { }
