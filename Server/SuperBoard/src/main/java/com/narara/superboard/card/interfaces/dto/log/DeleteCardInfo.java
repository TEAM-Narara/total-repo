package com.narara.superboard.card.interfaces.dto.log;

import com.narara.superboard.common.document.AdditionalDetails;

// Card 삭제 관련 정보
public record DeleteCardInfo(
        String cardName
) implements AdditionalDetails { }
