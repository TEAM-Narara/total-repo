package com.narara.superboard.reply.service;

import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.common.application.validator.ContentValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.authority.UnauthorizedException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
import com.narara.superboard.replymember.entity.ReplyMember;
import com.narara.superboard.replymember.infrastructure.ReplyMemberRepository;
import java.util.List;

import com.narara.superboard.websocket.enums.ReplyAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.narara.superboard.websocket.enums.ReplyAction.DELETE_REPLY;
import static com.narara.superboard.websocket.enums.ReplyAction.EDIT_REPLY;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;
    private final CardRepository cardRepository;
    private final ReplyMemberRepository replyMemberRepository;

    private final ContentValidator contentValidator;

    @Override
    public Reply createReply(Member member, ReplyCreateRequestDto replyCreateRequestDto) {
        contentValidator.validateReplyContentIsEmpty(replyCreateRequestDto);

        Card card = cardRepository.findById(replyCreateRequestDto.cardId())
                .orElseThrow(() -> new NotFoundEntityException(replyCreateRequestDto.cardId(), "카드"));

//        TODO: 댓글을 포함하는 보드의 권한이 있는지 확인

        Reply reply = Reply.createReply(replyCreateRequestDto, card);
        Reply savedReply = replyRepository.save(reply);

        ReplyMember replyMember = ReplyMember.createReplyMember(savedReply, member);
        replyMemberRepository.save(replyMember);

        return savedReply;
    }

    @Override
    public Reply getReply(Long replyId) {
//        TODO: 댓글을 포함하는 보드의 권한이 있는지 확인
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundEntityException(replyId, "댓글"));
    }

    @Override
    public Reply updateReply(Member member, Long replyId, ReplyUpdateRequestDto replyUpdateRequestDto) {
        contentValidator.validateReplyContentIsEmpty(replyUpdateRequestDto);

        // 기존 댓글이 존재하는지 확인
        Reply reply = getReply(replyId);

        if (!member.getId().equals(reply.getMember().getId())){
            throw new UnauthorizedException(member.getNickname(), EDIT_REPLY);
        }
        // 댓글 내용 업데이트
        return reply.updateReply(replyUpdateRequestDto);
    }

    @Override
    public Reply deleteReply(Member member, Long replyId) {
        Reply reply = getReply(replyId);
        if (!member.getId().equals(reply.getMember().getId())){
            throw new UnauthorizedException(member.getNickname(), DELETE_REPLY);
        }
        return reply.deleteReply();
    }

    @Override
    public List<Reply> getRepliesByCardId(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));

        return replyRepository.findAllByCard(card);
    }
}
