package com.narara.superboard.card.interfaces.dto.log;

import com.narara.superboard.common.document.AdditionalDetails;

// Card 생성 관련 정보
public record CreateCardInfo(
        String cardName
) implements AdditionalDetails { }
