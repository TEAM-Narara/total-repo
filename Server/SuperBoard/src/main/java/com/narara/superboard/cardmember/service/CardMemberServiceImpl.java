package com.narara.superboard.cardmember.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.cardmember.infrastructure.CardMemberRepository;
import com.narara.superboard.cardmember.interfaces.dto.UpdateCardMemberRequestDto;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardMemberServiceImpl implements CardMemberService {
    private final CardMemberRepository cardMemberRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;

    @Override
    public boolean getCardMemberIsAlert(Long memberId, Long cardId) {
        validateCardExists(cardId);

        return cardMemberRepository.findByCardIdAndMemberId(cardId, memberId)
                .map(CardMember::isAlert)
                .orElse(false);
    }

    @Override
    public void setCardMemberIsAlert(Long memberId, Long cardId) {
        Card card = validateCardExists(cardId);
        Member member = validateMemberExists(memberId);

        cardMemberRepository.findByCardIdAndMemberId(cardId, memberId)
                .ifPresentOrElse(
                        this::toggleAlertAndSave,
                        () -> addNewCardMember(member, card)
                );
    }

    @Override
    public void updateCardMembers(UpdateCardMemberRequestDto updateCardMemberRequestDto) {
        Card card = validateCardExists(updateCardMemberRequestDto.cardId());

    }

    // 카드 존재 확인 및 조회
    private Card validateCardExists(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
    }

    // 멤버 존재 확인 및 조회
    private Member validateMemberExists(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException(memberId, "멤버"));
    }

    // CardMember 객체의 isAlert 상태를 반대로 변경하고 저장
    private void toggleAlertAndSave(CardMember cardMember) {
        cardMember.changeIsAlert();
        cardMemberRepository.save(cardMember);
    }

    // 새로운 CardMember 추가 및 저장
    private void addNewCardMember(Member member, Card card) {
        CardMember newCardMember = CardMember.builder()
                .member(member)
                .card(card)
                .isAlert(true)
                .build();
        cardMemberRepository.save(newCardMember);
    }

}
