package com.narara.superboard.reply.service;

import com.narara.superboard.MockSuperBoardUnitTests;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastrucuture.CardRepository;
import com.narara.superboard.common.application.validator.ContentValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("댓글 생성시, 카드가 존재하지 않을 때 NotFoundEntityException 발생")
    void shouldThrowExceptionWhenCardDoesNotExist() {
        // given
        ReplyCreateRequestDto requestDto = new ReplyCreateRequestDto(1L, "Valid content");

        // Mocking: 카드가 존재하지 않도록 설정
        when(cardRepository.findById(requestDto.cardId())).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundEntityException.class, () -> replyService.createReply(requestDto));
    }

    @Test
    @DisplayName("유효한 데이터로 Reply 생성 성공")
    void shouldCreateReplySuccessfullyWhenValidDataIsGiven() {
        // given
        ReplyCreateRequestDto requestDto = new ReplyCreateRequestDto(1L, "Valid content");
        Card card = Card.builder().id(requestDto.cardId()).name("Test Card").build();
        Reply expectedReply = Reply.builder().id(1L).content(requestDto.content()).card(card).build();

        // Mocking: 검증 로직을 모킹
        doNothing().when(contentValidator).validateReplyContentIsEmpty(requestDto);
        when(cardRepository.findById(requestDto.cardId())).thenReturn(Optional.of(card));

        // when
        Reply result = replyService.createReply(requestDto);

        // then
        assertNotNull(result);
        assertEquals(expectedReply.getContent(), result.getContent());
        assertEquals(expectedReply.getCard(), result.getCard());

        verify(contentValidator, times(1)).validateReplyContentIsEmpty(requestDto);
        verify(cardRepository, times(1)).findById(requestDto.cardId());
    }

}
