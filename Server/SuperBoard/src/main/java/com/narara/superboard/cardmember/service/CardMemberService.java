package com.narara.superboard.cardmember.service;

public interface CardMemberService {
    boolean getCardMemberIsAlert(Long memberId, Long cardId);
    void setCardMemberIsAlert(Long memberId, Long cardId);
}
