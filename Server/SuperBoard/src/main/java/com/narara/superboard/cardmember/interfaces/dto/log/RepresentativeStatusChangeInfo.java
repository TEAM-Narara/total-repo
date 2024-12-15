package com.narara.superboard.cardmember.interfaces.dto.log;


import com.narara.superboard.card.document.CardInfo;

// CardMember 대표 상태 변경 관련 정보
public record RepresentativeStatusChangeInfo(
        Long memberId,
        String memberNickname,
        Long cardId,
        String cardName,
        boolean isRepresentative
) implements CardInfo {
}
