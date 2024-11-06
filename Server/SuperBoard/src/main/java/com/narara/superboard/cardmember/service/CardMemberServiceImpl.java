package com.narara.superboard.cardmember.service;

import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardHistoryRepository;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.cardmember.infrastructure.CardMemberRepository;
import com.narara.superboard.cardmember.interfaces.dto.UpdateCardMemberRequestDto;
import com.narara.superboard.cardmember.interfaces.dto.log.RepresentativeStatusChangeInfo;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.document.Target;
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
    private final CardHistoryRepository cardHistoryRepository;

    @Override
    public boolean getCardMemberIsAlert(Long memberId, Long cardId) {
        validateCardExists(cardId);
        validateMemberExists(memberId);

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
    public void setCardMemberIsRepresentative(UpdateCardMemberRequestDto updateCardMemberRequestDto) {
        Card card = validateCardExists(updateCardMemberRequestDto.cardId());
        Member member = validateMemberExists(updateCardMemberRequestDto.memberId());

        cardMemberRepository.findByCardIdAndMemberId(
                        updateCardMemberRequestDto.cardId(), updateCardMemberRequestDto.memberId())
                .ifPresentOrElse(
                        cardMember -> {
                            toggleRepresentativeAndSave(cardMember);

                            // 로그 기록 추가
                            RepresentativeStatusChangeInfo repStatusChangeInfo = new RepresentativeStatusChangeInfo(
                                    member.getId(), card.getId(), cardMember.isRepresentative());
                            Target target = Target.of(card, repStatusChangeInfo);

                            CardHistory cardHistory = CardHistory.careateCardHistory(
                                    member, System.currentTimeMillis(), card.getList().getBoard(), card,
                                    EventType.UPDATE, EventData.CARD_MANAGER, target);

                            cardHistoryRepository.save(cardHistory);
                        },
                        () -> addNewRepresentativeCardMemberWithLog(member, card)
                );
    }

    // CardMember 대표 멤버 추가와 로그 저장을 함께 수행
    private void addNewRepresentativeCardMemberWithLog(Member member, Card card) {
        CardMember newCardMember = addNewRepresentativeCardMember(member, card);

        // 로그 기록 추가
        RepresentativeStatusChangeInfo newRepCardMemberInfo = new RepresentativeStatusChangeInfo(
                member.getId(), card.getId(), newCardMember.isRepresentative());
        Target target = Target.of(card, newRepCardMemberInfo);

        CardHistory cardHistory = CardHistory.careateCardHistory(
                member, System.currentTimeMillis(), card.getList().getBoard(), card,
                EventType.ADD, EventData.CARD_MANAGER, target);

        cardHistoryRepository.save(cardHistory);
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

    // CardMember 객체의 isRepresentative 상태를 반대로 변경하고, isAlert도 동일한 값으로 설정 후 저장
    private void toggleRepresentativeAndSave(CardMember cardMember) {
        cardMember.changeIsRepresentative();
        cardMember.setAlert(cardMember.isRepresentative());
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

    // 새로운 CardMember 추가 및 저장
    private CardMember addNewRepresentativeCardMember(Member member, Card card) {
        CardMember newCardMember = CardMember.builder()
                .member(member)
                .card(card)
                .isAlert(true)
                .isRepresentative(true)
                .build();
        cardMemberRepository.save(newCardMember);
        return newCardMember;
    }
}
