package com.narara.superboard.reply.service;

import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardHistoryRepository;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.service.CardService;
import com.narara.superboard.common.application.validator.ContentValidator;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.document.Target;
import com.narara.superboard.common.exception.DeletedEntityException;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.authority.UnauthorizedException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.CreateReplyInfo;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
import com.narara.superboard.websocket.enums.ReplyAction;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.narara.superboard.websocket.enums.ReplyAction.DELETE_REPLY;
import static com.narara.superboard.websocket.enums.ReplyAction.EDIT_REPLY;


@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{

    private final CardService cardService;

    private final ReplyRepository replyRepository;
    private final CardRepository cardRepository;
    private final CardHistoryRepository cardHistoryRepository;

    private final ContentValidator contentValidator;

    @Override
    @Transactional
    public Reply createReply(Member member, ReplyCreateRequestDto replyCreateRequestDto) {
        contentValidator.validateReplyContentIsEmpty(replyCreateRequestDto);

        Card card = cardRepository.findById(replyCreateRequestDto.cardId())
                .orElseThrow(() -> new NotFoundEntityException(replyCreateRequestDto.cardId(), "카드"));

        cardService.checkBoardMember(card,member, ReplyAction.ADD_REPLY);

        Reply reply = Reply.createReply(replyCreateRequestDto, card, member);

        Reply savedReply = replyRepository.save(reply);


        CreateReplyInfo createReplyInfo = new CreateReplyInfo(reply.getContent());
        Target target = Target.of(savedReply, createReplyInfo);

        CardHistory cardHistory = CardHistory.careateCardHistory(
                member, savedReply.getUpdatedAt(), card.getList().getBoard(), card,
                EventType.CREATE, EventData.COMMENT, target);

        cardHistoryRepository.save(cardHistory);

        return savedReply;
    }

    @Override
    public Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundEntityException(replyId, "댓글"));
    }

    @Override
    public Reply updateReply(Member member, Long replyId, ReplyUpdateRequestDto replyUpdateRequestDto) {
        contentValidator.validateReplyContentIsEmpty(replyUpdateRequestDto);

        // 기존 댓글이 존재하는지 확인
        Reply reply = getReply(replyId);
        if (reply.getIsDeleted()){
            throw new DeletedEntityException(replyId, "댓글");
        }

        // 이 방식이 성능이 더 중요함.
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
    public class CustomTestException extends RuntimeException {
        public CustomTestException(String message) {
            super(message);
            System.out.println("excetion");
        }
    }

}

