package com.narara.superboard.card.interfaces.dto.log;

import com.narara.superboard.common.document.Target;

// Card 생성 관련 정보
public record CreateCardInfo(
        Long cardId,
        String cardName
) implements Target { }
