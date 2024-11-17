package com.narara.superboard.cardmember.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
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
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.fcmtoken.service.AlarmService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardMemberServiceImpl implements CardMemberService {
    private final CardMemberRepository cardMemberRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;
    private final CardHistoryRepository cardHistoryRepository;
    private final BoardOffsetService boardOffsetService;

    private final AlarmService alarmService;

    @Override
    public boolean getCardMemberIsAlert(Member member, Long cardId) {
        validateCardExists(cardId);

        return cardMemberRepository.findByCardIdAndMember(cardId, member)
                .map(CardMember::isAlert)
                .orElse(false);
    }

    @Override
    public Boolean setCardMemberIsAlert(Member member, Long cardId) {
        Card card = validateCardExists(cardId);

        return cardMemberRepository.findByCardIdAndMember(cardId, member)
                .map(cardMember -> {
                    toggleAlertAndSave(cardMember);
                    return cardMember.isAlert(); // 현재 알림 여부 반환
                })
                .orElseGet(() -> {
                    addNewCardMember(member, card);
                    return true; // 새로운 CardMember 추가 시 알림이 활성화됨
                });
    }

    @Override
    public Boolean setCardMemberIsRepresentative(Member manOfAction, UpdateCardMemberRequestDto updateCardMemberRequestDto) {
        Card card = validateCardExists(updateCardMemberRequestDto.cardId());
        Member inviteMember = validateMemberExists(updateCardMemberRequestDto.memberId());

        return cardMemberRepository.findByCardIdAndMemberId(
                        updateCardMemberRequestDto.cardId(), updateCardMemberRequestDto.memberId())
                .map(cardMember -> {
                    toggleRepresentativeAndSave(cardMember);

                    if (cardMember.isRepresentative()) { // Websocket 카드멤버 추가
                        boardOffsetService.saveAddCardMember(cardMember);

                        //알람
                        try {
                            alarmService.sendAddCardMemberAlarm(manOfAction, cardMember);
                        } catch (FirebaseMessagingException e) {
                            log.info("알람에러: sendAddCardMemberAlarm \n" + e.getMessage());
                        }
                    } else {
                        boardOffsetService.saveDeleteCardMember(cardMember);

                        //알람
                        try {
                            alarmService.sendDeleteCardMemberAlarm(manOfAction, cardMember);
                        } catch (FirebaseMessagingException e) {
                            log.info("알람에러: sendAddCardMemberAlarm \n" + e.getMessage());
                        }
                    }

                    // 로그 기록 추가
                    RepresentativeStatusChangeInfo repStatusChangeInfo = new RepresentativeStatusChangeInfo(
                            inviteMember.getId(), inviteMember.getNickname(), card.getId(), card.getName(), cardMember.isRepresentative());

                    CardHistory<RepresentativeStatusChangeInfo> cardHistory = CardHistory.createCardHistory(
                            inviteMember, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), card.getList().getBoard(), card,
                            EventType.UPDATE, EventData.CARD_MANAGER, repStatusChangeInfo);

                    cardHistoryRepository.save(cardHistory);

                    return cardMember.isRepresentative(); // 현재 대표자 여부 반환
                })
                .orElseGet(() -> {
                    addNewRepresentativeCardMemberWithLog(inviteMember, card);
                    return true; // 새로운 대표자 추가 시 대표자로 설정됨
                });
    }


    // CardMember 대표 멤버 추가와 로그 저장을 함께 수행
    private void addNewRepresentativeCardMemberWithLog(Member member, Card card) {
        CardMember newCardMember = addNewRepresentativeCardMember(member, card);

        // 로그 기록 추가
        RepresentativeStatusChangeInfo newRepCardMemberInfo = new RepresentativeStatusChangeInfo(
                member.getId(), member.getNickname(), card.getId(), card.getName(),newCardMember.isRepresentative());

        CardHistory<RepresentativeStatusChangeInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), card.getList().getBoard(), card,
                EventType.ADD, EventData.CARD_MANAGER, newRepCardMemberInfo);

        cardHistoryRepository.save(cardHistory);
    }

    // 카드 존재 확인 및 조회
    private Card validateCardExists(Long cardId) {
        return cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
    }

    // 멤버 존재 확인 및 조회
    private Member validateMemberExists(Long memberId) {
        return memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new NotFoundEntityException(memberId, "멤버"));
    }

    // CardMember 객체의 isAlert 상태를 반대로 변경하고 저장
    private void toggleAlertAndSave(CardMember cardMember) {
        cardMember.changeIsAlert();
        cardMemberRepository.save(cardMember);
    }

    // CardMember 객체의 isRepresentative 상태를 반대로 변경하고, isAlert 도 동일한 값으로 설정 후 저장
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
