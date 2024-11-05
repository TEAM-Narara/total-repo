package com.narara.superboard.cardmember.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.cardmember.infrastructure.CardMemberRepository;
import com.narara.superboard.cardmember.interfaces.dto.UpdateCardMemberRequestDto;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.MemberNotFoundException;
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

        Card card = new Card(cardId, null, "dd", "dd", null, null, null, null, null, null, null, null, null);
        Member member = new Member(memberId, "dd", "ddd@naver.com");

        // 카드와 멤버 유효성 확인
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // 카드가 존재한다고 가정
        when(cardRepository.existsById(cardId)).thenReturn(true);

        CardMember cardMember = new CardMember(member, card, true,false);

        when(cardMemberRepository.findByCardIdAndMemberId(cardId, memberId))
                .thenReturn(Optional.of(cardMember));

        // 메서드 호출 및 검증
        boolean result = cardMemberService.getCardMemberIsAlert(memberId, cardId);
        assertTrue(result, "watch 상태가 true일 때 true를 반환해야 합니다.");

        // 리포지토리 메서드 호출 확인
        verify(cardRepository, times(1)).findById(cardId);
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
        verify(cardRepository, times(1)).findById(cardId);
        verify(cardMemberRepository, never()).findByCardIdAndMemberId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("카드 개인 알림 설정 조회 테스트 - card는 있는데 CardMember에 없으면 false 처리")
    void testGetCardMemberWatch_CardExistsButNoCardMember() {
        Long cardId = card.getId();
        Long memberId = member.getId();

        // 카드와 멤버 유효성 확인
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(cardMemberRepository.findByCardIdAndMemberId(cardId, memberId))
                .thenReturn(Optional.empty());

        boolean result = cardMemberService.getCardMemberIsAlert(memberId, cardId);

        assertFalse(result, "카드는 존재하지만 CardMember에 없으면 false를 반환해야 합니다.");

        verify(cardRepository, times(1)).findById(cardId);
        verify(cardMemberRepository, times(1)).findByCardIdAndMemberId(cardId, memberId);
    }

    @Test
    @DisplayName("카드와 멤버가 존재할 때 watch 상태를 반대로 변경")
    void testSetCardMemberWatch_CardExistsAndMemberExists() {
        Long cardId = card.getId();
        Long memberId = member.getId();
        CardMember existingCardMember = new CardMember(member, card, true);

        // 카드와 멤버 유효성 확인
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(cardMemberRepository.findByCardIdAndMemberId(cardId, memberId))
                .thenReturn(Optional.of(existingCardMember));

        cardMemberService.setCardMemberIsAlert(memberId, cardId);

        assertFalse(existingCardMember.isAlert());
        verify(cardRepository, times(1)).findById(cardId);
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
        verify(cardRepository, times(1)).findById(cardId);
        verify(cardMemberRepository, never()).findByCardIdAndMemberId(anyLong(), anyLong());
        verify(cardMemberRepository, never()).save(any(CardMember.class));
    }

    /**
     * 카드 담당자 수정 TEST ---------------------------------------------------------------------
     */
    @Test
    @DisplayName("카드 멤버 추가 또는 대표 상태 업데이트")
    void setCardMemberIsRepresentative() {
        // given
        long cardId = 1L;
        long memberId = 1L;
        boolean isRepresentative = true; // 요청 DTO의 초기 대표 상태 값
        boolean isAlert = true;

        UpdateCardMemberRequestDto updateCardMemberRequestDto = new UpdateCardMemberRequestDto(cardId, memberId);

        Card card = new Card(cardId, null, "dd", "dd", null, null, null, null, null, null, null, null, null);
        Member member = new Member(memberId, "dd", "ddd@naver.com");

        CardMember cardMember = new CardMember(member, card, isAlert,isRepresentative);

        // 카드와 멤버 유효성 확인
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // 3번 조건: 카드 멤버가 없는 경우 새로 추가
        when(cardMemberRepository.findByCardIdAndMemberId(cardId, memberId)).thenReturn(Optional.empty());

        // 4번 조건: 카드 멤버가 이미 존재하는 경우 대표 상태 반대로 변경
        when(cardMemberRepository.findByCardIdAndMemberId(cardId, memberId)).thenReturn(Optional.of(cardMember));

        // when
        cardMemberService.setCardMemberIsRepresentative(updateCardMemberRequestDto);

        // then
        if (cardMemberRepository.findByCardIdAndMemberId(cardId, memberId).isEmpty()) {
            verify(cardMemberRepository, times(1)).save(any(CardMember.class));
        } else {
            verify(cardMemberRepository, times(1)).save(cardMember);
            assertEquals(!isRepresentative, cardMember.isRepresentative()); // 대표 상태가 반대로 변경되었는지 확인
        }
    }

    @Test
    @DisplayName("카드가 존재하지 않을 때 예외 발생 테스트")
    void setCardMemberIsRepresentative_cardNotFound() {
        // given
        long cardId = 1L;
        long memberId = 1L;
        boolean isRepresentative = true;

        UpdateCardMemberRequestDto updateCardMemberRequestDto = new UpdateCardMemberRequestDto(cardId, memberId);

        // 카드가 존재하지 않는 경우
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cardMemberService.setCardMemberIsRepresentative(updateCardMemberRequestDto);
        });

        verify(cardRepository, times(1)).findById(cardId);
        verify(cardMemberRepository, never()).findByCardIdAndMemberId(anyLong(), anyLong());
        verify(cardMemberRepository, never()).save(any(CardMember.class));
    }

    @Test
    @DisplayName("멤버가 유효하지 않은 경우 예외 발생 테스트")
    void setCardMemberIsRepresentative_invalidMember() {
        // given
        long cardId = 1L;
        long memberId = 2L; // 유효하지 않은 멤버 ID
        boolean isRepresentative = true;

        UpdateCardMemberRequestDto updateCardMemberRequestDto = new UpdateCardMemberRequestDto(cardId, memberId);

        Card card = new Card(cardId, null, "dd", "dd", null, null, null, null, null, null, null, null, null);

        // 카드가 존재하지만 멤버가 유효하지 않은 경우
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(NotFoundEntityException.class, () -> {
            cardMemberService.setCardMemberIsRepresentative(updateCardMemberRequestDto);
        });

        verify(cardRepository, times(1)).findById(cardId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(cardMemberRepository, never()).save(any(CardMember.class));
    }
}