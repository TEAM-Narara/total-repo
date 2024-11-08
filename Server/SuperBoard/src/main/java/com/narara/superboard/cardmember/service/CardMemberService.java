package com.narara.superboard.cardmember.service;

import com.narara.superboard.cardmember.interfaces.dto.UpdateCardMemberRequestDto;
import com.narara.superboard.member.entity.Member;

public interface CardMemberService {
    boolean getCardMemberIsAlert(Member member, Long cardId);
    void setCardMemberIsAlert(Member member, Long cardId);
    void setCardMemberIsRepresentative(UpdateCardMemberRequestDto updateCardMemberRequestDto);
}
