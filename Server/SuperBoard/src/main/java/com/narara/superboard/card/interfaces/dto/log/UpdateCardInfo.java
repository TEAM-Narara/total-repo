package com.narara.superboard.card.interfaces.dto.log;

import com.narara.superboard.common.document.AdditionalDetails;

// Card 수정 관련 정보
public record UpdateCardInfo(
        String cardName
) implements AdditionalDetails { }
