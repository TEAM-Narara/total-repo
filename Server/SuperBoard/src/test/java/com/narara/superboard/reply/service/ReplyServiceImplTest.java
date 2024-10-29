package com.narara.superboard.reply.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.common.application.validator.ContentValidator;
import com.narara.superboard.common.exception.NotFoundContentException;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
import com.narara.superboard.replymember.entity.ReplyMember;
import com.narara.superboard.replymember.infrastructure.ReplyMemberRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("댓글 서비스에 대한 단위 테스트")
class ReplyServiceImplTest implements MockSuperBoardUnitTests {

    @InjectMocks
    private ReplyServiceImpl replyService;

    @Mock
    private ContentValidator contentValidator;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ReplyMemberRepository replyMemberRepository;

    @Mock
    private ReplyRepository replyRepository;

    @Test
    @DisplayName("댓글 생성시, 카드가 존재하지 않을 때 NotFoundEntityException 발생")
    void shouldThrowExceptionWhenCardDoesNotExist() {
        // given
        ReplyCreateRequestDto requestDto = new ReplyCreateRequestDto(1L, "Valid content");
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Mocking: 카드가 존재하지 않도록 설정
        when(cardRepository.findById(requestDto.cardId())).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundEntityException.class, () -> replyService.createReply(member, requestDto));
    }

    @Test
    @DisplayName("유효한 데이터로 Reply 생성 성공")
    void shouldCreateReplySuccessfullyWhenValidDataIsGiven() {
        // given
        ReplyCreateRequestDto requestDto = new ReplyCreateRequestDto(1L, "Valid content");
        Card card = Card.builder().id(requestDto.cardId()).name("Test Card").build();
        Reply expectedReply = Reply.builder().id(1L).content(requestDto.content()).card(card).build();
        Member member = new Member(1L, "시현", "sisi@naver.com");

        // Mocking: 검증 로직을 모킹
        doNothing().when(contentValidator).validateReplyContentIsEmpty(requestDto);
        when(cardRepository.findById(requestDto.cardId())).thenReturn(Optional.of(card));
        when(replyRepository.save(any(Reply.class))).thenReturn(expectedReply);  // Mocking save 결과

        // when
        Reply result = replyService.createReply(member, requestDto);

        // then
        assertNotNull(result);  // result가 null이 아님을 확인
        assertEquals(expectedReply.getContent(), result.getContent());
        assertEquals(expectedReply.getCard(), result.getCard());

        verify(contentValidator, times(1)).validateReplyContentIsEmpty(requestDto);
        verify(cardRepository, times(1)).findById(requestDto.cardId());
        verify(replyRepository, times(1)).save(any(Reply.class));
        verify(replyMemberRepository, times(1)).save(any(ReplyMember.class));
    }


    @Test
    @DisplayName("존재하지 않는 Reply ID로 조회 시 NotFoundEntityException 발생")
    void shouldThrowExceptionWhenReplyNotFound() {
        // given
        Long nonExistentReplyId = 1L;

        // Mocking: Reply가 존재하지 않도록 설정
        when(replyRepository.findById(nonExistentReplyId)).thenReturn(Optional.empty());

        // then: 예외 발생 확인
        NotFoundEntityException exception = assertThrows(
                NotFoundEntityException.class,
                () -> replyService.getReply(nonExistentReplyId)
        );
        assertEquals("해당하는 댓글(이)가 존재하지 않습니다. 댓글ID: " + nonExistentReplyId, exception.getMessage());

        verify(replyRepository, times(1)).findById(nonExistentReplyId);
    }

    @Test
    @DisplayName("존재하는 Reply ID로 조회 시 성공적으로 Reply 반환")
    void shouldReturnReplyWhenReplyExists() {
        // given
        Long replyId = 1L;
        Reply reply = Reply.builder()
                .id(replyId)
                .content("This is a test reply.")
                .build();

        // Mocking: Reply가 존재하도록 설정
        when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));

        // when
        Reply result = replyService.getReply(replyId);

        // then
        assertNotNull(result);
        assertEquals(replyId, result.getId());
        assertEquals("This is a test reply.", result.getContent());

        verify(replyRepository, times(1)).findById(replyId);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("내용이 비어있을 때 ContentNotFoundException 발생")
    void shouldThrowExceptionWhenContentIsEmpty(String invalidContent) {
        // given
        ReplyUpdateRequestDto requestDto = new ReplyUpdateRequestDto(invalidContent);

        // Mocking: contentValidator가 예외를 던지도록 설정
        doThrow(new NotFoundContentException("댓글")).when(contentValidator).validateReplyContentIsEmpty(requestDto);

        // then
        assertThrows(NotFoundContentException.class, () -> replyService.updateReply(1L, requestDto));
    }

    @Test
    @DisplayName("댓글 업데이트 성공 테스트")
    void updateReply_ShouldUpdateReplySuccessfully() {
        // given
        Long replyId = 1L;
        String updatedContent = "Updated Reply Content";
        ReplyUpdateRequestDto requestDto = new ReplyUpdateRequestDto(updatedContent);

        Reply existingReply = Reply.builder()
                .id(replyId)
                .content("Original Content")
                .build();

        when(replyRepository.findById(replyId)).thenReturn(Optional.of(existingReply));

        // when
        Reply updatedReply = replyService.updateReply(replyId, requestDto);

        // then
        assertEquals(updatedContent, updatedReply.getContent());
        verify(replyRepository, times(1)).findById(replyId);
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void shouldDeleteReplySuccessfully() {
        // given
        Long replyId = 1L;
        Reply reply = Reply.builder()
                .id(replyId)
                .content("This is a test reply")
                .build();

        // Mocking: getReply가 호출될 때 reply 객체를 반환하도록 설정
        when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));

        // when
        replyService.deleteReply(replyId);

        // then
        // delete 메서드가 올바르게 호출되었는지 확인
        verify(replyRepository, times(1)).delete(reply);
        verify(replyRepository, times(1)).findById(replyId);  // findById가 1번 호출되었는지 확인
    }

    @Test
    @DisplayName("카드 ID로 카드가 존재하지 않을 때 NotFoundEntityException 발생")
    void shouldThrowExceptionWhenCardNotFound() {
        // given: cardId로 카드가 존재하지 않도록 설정
        Long cardId = 1L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when & then: 예외가 발생하는지 확인
        NotFoundEntityException exception = assertThrows(
                NotFoundEntityException.class,
                () -> replyService.getRepliesByCardId(cardId)
        );

        assertEquals("해당하는 카드(이)가 존재하지 않습니다. 카드ID: " + cardId, exception.getMessage());
        verify(cardRepository, times(1)).findById(cardId);
        verify(replyRepository, never()).findAllByCard(any(Card.class));
    }

    @Test
    @DisplayName("카드에 댓글이 없을 때 빈 리스트 반환")
    void shouldReturnEmptyListWhenNoRepliesForCard() {
        // given
        Long cardId = 1L;
        Card card = Card.builder().id(cardId).name("Test Card").build();

        // Mocking
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(replyRepository.findAllByCard(card)).thenReturn(Collections.emptyList());

        // when
        List<Reply> replies = replyService.getRepliesByCardId(cardId);

        // then
        assertTrue(replies.isEmpty(), "댓글 리스트가 비어 있어야 합니다.");
        verify(cardRepository, times(1)).findById(cardId);
        verify(replyRepository, times(1)).findAllByCard(card);
    }

    @Test
    @DisplayName("카드에 두 개 이상의 댓글이 있을 때 리스트 반환")
    void shouldReturnRepliesListWhenRepliesExistForCard() {
        // given
        Long cardId = 1L;
        Card card = Card.builder().id(cardId).name("Test Card").build();

        Reply reply1 = Reply.builder().id(1L).content("First reply").card(card).build();
        Reply reply2 = Reply.builder().id(2L).content("Second reply").card(card).build();
        List<Reply> replyList = Arrays.asList(reply1, reply2);

        // Mocking
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(replyRepository.findAllByCard(card)).thenReturn(replyList);

        // when
        List<Reply> replies = replyService.getRepliesByCardId(cardId);

        // then
        assertEquals(2, replies.size(), "댓글 리스트 크기는 2여야 합니다.");
        assertEquals("First reply", replies.get(0).getContent());
        assertEquals("Second reply", replies.get(1).getContent());
        verify(cardRepository, times(1)).findById(cardId);
        verify(replyRepository, times(1)).findAllByCard(card);
    }
}
