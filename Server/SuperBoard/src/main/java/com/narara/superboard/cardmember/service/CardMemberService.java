package com.narara.superboard.cardmember.service;

import com.narara.superboard.cardmember.interfaces.dto.UpdateCardMemberRequestDto;

public interface CardMemberService {
    boolean getCardMemberIsAlert(Long memberId, Long cardId);
    void setCardMemberIsAlert(Long memberId, Long cardId);
    void updateCardMembers(UpdateCardMemberRequestDto updateCardMemberRequestDto);
}
