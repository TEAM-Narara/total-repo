package com.narara.superboard.cardmember.service;

public interface CardMemberService {
    // TODO : 카드 개인 알림 설정 조회
    boolean getCardMemberWatch(String memberId, String cardId);
    // TODO : 카드 개인 알림 반대로 수정
    void setCardMemberWatch(String memberId, String cardId);
}
