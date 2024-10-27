package com.narara.superboard.reply.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastrucuture.CardRepository;
import com.narara.superboard.common.application.validator.ContentValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
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

    @Override
    public Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundEntityException(replyId, "댓글"));
    }

    @Override
    public Reply updateReply(Long replyId, ReplyUpdateRequestDto replyUpdateRequestDto) {
        contentValidator.validateReplyContentIsEmpty(replyUpdateRequestDto);

        // 기존 댓글이 존재하는지 확인
        Reply reply = getReply(replyId);

        // 댓글 내용 업데이트
        return reply.updateReply(replyUpdateRequestDto);
    }

    @Override
    public void deleteReply(Long replyId) {
        Reply reply = getReply(replyId);
        replyRepository.delete(reply);
    }

}
