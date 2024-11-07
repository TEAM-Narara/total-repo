package com.narara.superboard.cardmember.interfaces.dto.log;



// CardMember 대표 상태 변경 관련 정보
public record RepresentativeStatusChangeInfo(
        Long memberId,
        String memberNickname,
        Long cardId,
        String cardName,
        boolean isRepresentative
) {
}
