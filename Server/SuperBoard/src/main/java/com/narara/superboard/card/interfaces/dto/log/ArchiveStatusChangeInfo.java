package com.narara.superboard.card.interfaces.dto.log;



// Card 아카이브 상태 변경 관련 정보
public record ArchiveStatusChangeInfo(
        Long cardId,
        String cardName,
        boolean isArchived
) { }
