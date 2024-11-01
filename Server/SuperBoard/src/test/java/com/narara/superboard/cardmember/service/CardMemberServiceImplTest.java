package com.narara.superboard.cardmember.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.cardmember.infrastructure.CardMemberRepository;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardMemberServiceImplTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMemberRepository cardMemberRepository;

    @Mock
    private MemberRepository memberRepository;


    @InjectMocks
    private CardMemberServiceImpl cardMemberService;
    private Member member;
    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Long cardId = 1L;
        Long memberId = 1L;

        // Member 및 Card 객체 초기화
        member = new Member(memberId, "username", "password");
        card = Card.builder()
                .id(cardId)
                .name("dd")
                .myOrder(1L)
                .myOrder(0L)
                .isDeleted(false)
                .isArchived(false)
                .build();
    }

    @Test
    @DisplayName("카드 개인 알림 설정 조회 성공 테스트 - 카드와 멤버가 존재하고, watch 상태가 true일 때 true를 반환")
    void testGetCardMemberWatch_CardExistsAndWatchIsTrue() {
        Long cardId = card.getId();
        Long memberId = member.getId();

        // 카드가 존재한다고 가정
        when(cardRepository.existsById(cardId)).thenReturn(true);

        // CardMember 객체를 생성하여 watch 상태가 true로 설정되었다고 가정
        CardMember cardMember = new CardMember(member, card, true);
        when(cardMemberRepository.findByCardIdAndMemberId(cardId, memberId))
                .thenReturn(Optional.of(cardMember));

        // 메서드 호출 및 검증
        boolean result = cardMemberService.getCardMemberIsAlert(memberId, cardId);
        assertTrue(result, "watch 상태가 true일 때 true를 반환해야 합니다.");

        // 리포지토리 메서드 호출 확인
        verify(cardRepository, times(1)).existsById(cardId);
        verify(cardMemberRepository, times(1)).findByCardIdAndMemberId(cardId, memberId);
    }

    @Test
    @DisplayName("카드 개인 알림 설정 조회 실패 테스트 - 카드가 존재하지 않을 때 예외 발생")
    void testGetCardMemberWatch_CardDoesNotExist() {
        Long cardId = card.getId();
        Long memberId = member.getId();

        // 카드가 존재하지 않도록 설정
        when(cardRepository.existsById(cardId)).thenReturn(false);

        // 메서드 호출 시 예외가 발생하는지 검증
        assertThrows(IllegalArgumentException.class, () -> {
            cardMemberService.getCardMemberIsAlert(memberId, cardId);
        }, "Card with ID " + cardId + " does not exist.");

        // cardRepository는 존재 확인을 위해 호출되지만 cardMemberRepository는 호출되지 않아야 함
        verify(cardRepository, times(1)).existsById(cardId);
        verify(cardMemberRepository, never()).findByCardIdAndMemberId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("카드 개인 알림 설정 조회 테스트 - card는 있는데 CardMember에 없으면 false 처리")
    void testGetCardMemberWatch_CardExistsButNoCardMember() {
        Long cardId = card.getId();
        Long memberId = member.getId();

        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(cardMemberRepository.findByCardIdAndMemberId(cardId, memberId))
                .thenReturn(Optional.empty());

        boolean result = cardMemberService.getCardMemberIsAlert(memberId, cardId);

        assertFalse(result, "카드는 존재하지만 CardMember에 없으면 false를 반환해야 합니다.");

        verify(cardRepository, times(1)).existsById(cardId);
        verify(cardMemberRepository, times(1)).findByCardIdAndMemberId(cardId, memberId);
    }

    @Test
    @DisplayName("카드와 멤버가 존재할 때 watch 상태를 반대로 변경")
    void testSetCardMemberWatch_CardExistsAndMemberExists() {
        Long cardId = card.getId();
        Long memberId = member.getId();
        CardMember existingCardMember = new CardMember(member, card, true);

        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(cardMemberRepository.findByCardIdAndMemberId(cardId, memberId))
                .thenReturn(Optional.of(existingCardMember));

        cardMemberService.setCardMemberIsAlert(memberId, cardId);

        assertFalse(existingCardMember.isAlert());
        verify(cardRepository, times(1)).existsById(cardId);
        verify(cardMemberRepository, times(1)).findByCardIdAndMemberId(cardId, memberId);
        verify(cardMemberRepository, times(1)).save(existingCardMember);
    }

    @Test
    @DisplayName("카드가 존재하고 멤버가 없을 때 watch 상태를 true로 설정")
    void testSetCardMemberWatch_CardExistsAndMemberDoesNotExist() {
        Long cardId = card.getId();
        Long memberId = member.getId();

        // 카드가 존재한다고 설정
        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // 멤버가 존재한다고 설정
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // CardMember가 존재하지 않는 경우
        when(cardMemberRepository.findByCardIdAndMemberId(cardId, memberId)).thenReturn(Optional.empty());

        // 메서드 호출
        cardMemberService.setCardMemberIsAlert(memberId, cardId);

        // 메서드 호출 횟수 검증
        verify(cardRepository, times(1)).existsById(cardId);
        verify(cardRepository, times(1)).findById(cardId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(cardMemberRepository, times(1)).findByCardIdAndMemberId(cardId, memberId);
        verify(cardMemberRepository, times(1)).save(any(CardMember.class));
    }
    @Test
    @DisplayName("카드가 존재하지 않을 때 예외 발생")
    void testSetCardMemberWatch_CardDoesNotExist() {
        Long cardId = card.getId();
        Long memberId = member.getId();

        when(cardRepository.existsById(cardId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> cardMemberService.setCardMemberIsAlert(memberId, cardId));
        verify(cardRepository, times(1)).existsById(cardId);
        verify(cardMemberRepository, never()).findByCardIdAndMemberId(anyLong(), anyLong());
        verify(cardMemberRepository, never()).save(any(CardMember.class));
    }
}