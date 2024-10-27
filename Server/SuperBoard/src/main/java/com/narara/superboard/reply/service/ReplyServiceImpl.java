package com.narara.superboard.reply.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastrucuture.CardRepository;
import com.narara.superboard.common.application.validator.ContentValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;
    private final CardRepository cardRepository;

    private final ContentValidator contentValidator;

    @Override
    public Reply createReply(ReplyCreateRequestDto replyCreateRequestDto) {
        contentValidator.validateReplyContentIsEmpty(replyCreateRequestDto);

        Card card = cardRepository.findById(replyCreateRequestDto.cardId())
                .orElseThrow(() -> new NotFoundEntityException(replyCreateRequestDto.cardId(), "카드"));

        Reply reply = Reply.createReply(replyCreateRequestDto, card);

        return replyRepository.save(reply);
    }
}
