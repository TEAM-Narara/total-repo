package com.narara.superboard.reply.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardHistoryRepository;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.service.CardService;
import com.narara.superboard.common.application.validator.ContentValidator;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.exception.DeletedEntityException;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.authority.UnauthorizedException;
import com.narara.superboard.fcmtoken.service.AlarmService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.infrastructure.ReplyRepository;
import com.narara.superboard.reply.interfaces.dto.ReplyInfo;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
import com.narara.superboard.websocket.enums.ReplyAction;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.narara.superboard.websocket.enums.ReplyAction.DELETE_REPLY;
import static com.narara.superboard.websocket.enums.ReplyAction.EDIT_REPLY;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{
    private final CardService cardService;

    private final ReplyRepository replyRepository;
    private final CardRepository cardRepository;
    private final CardHistoryRepository cardHistoryRepository;

    private final ContentValidator contentValidator;

    private final BoardOffsetService boardOffsetService;
    private final AlarmService alarmService;

    @Override
    @Transactional
    public Reply createReply(Member member, ReplyCreateRequestDto replyCreateRequestDto)
            throws FirebaseMessagingException {
        contentValidator.validateReplyContentIsEmpty(replyCreateRequestDto);

        Card card = cardRepository.findByIdAndIsDeletedFalse(replyCreateRequestDto.cardId())
                .orElseThrow(() -> new NotFoundEntityException(replyCreateRequestDto.cardId(), "카드"));

        cardService.checkBoardMember(card,member, ReplyAction.ADD_REPLY);

        Reply reply = Reply.createReply(replyCreateRequestDto, card, member);

        Reply savedReply = replyRepository.save(reply);

        boardOffsetService.saveAddReply(savedReply); //Websocket reply 추가

        ReplyInfo createReplyInfo = new ReplyInfo(card.getId(), card.getName(), reply.getId(), reply.getContent());

        CardHistory<ReplyInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), card.getList().getBoard(), card,
                EventType.CREATE, EventData.COMMENT, createReplyInfo);

        cardHistoryRepository.save(cardHistory);

        alarmService.sendAddReplyAlarm(member, savedReply);

        return savedReply;
    }

    @Override
    public Reply getReply(Long replyId) {
        return replyRepository.findByIdAndIsDeletedFalse(replyId)
                .orElseThrow(() -> new NotFoundEntityException(replyId, "댓글"));
    }

    @Override
    @Transactional
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
        reply.updateReply(replyUpdateRequestDto);
        boardOffsetService.saveEditReply(reply); //Websocket reply 업데이트

        // 업데이트 로그 기록
        ReplyInfo updateReplyInfo = new ReplyInfo(reply.getCard().getId(), reply.getCard().getName(), reply.getId(), reply.getContent());

        CardHistory<ReplyInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), reply.getCard().getList().getBoard(), reply.getCard(),
                EventType.UPDATE, EventData.COMMENT, updateReplyInfo);

        cardHistoryRepository.save(cardHistory);

        // 댓글 내용 업데이트
        return reply;
    }

    @Override
    @Transactional
    public Reply deleteReply(Member member, Long replyId) {
        Reply reply = getReply(replyId);
        if (!member.getId().equals(reply.getMember().getId())){
            throw new UnauthorizedException(member.getNickname(), DELETE_REPLY);
        }

        // 삭제 로그 기록
        ReplyInfo deleteReplyInfo = new ReplyInfo(reply.getCard().getId(), reply.getCard().getName(),reply.getId(), reply.getContent());

        CardHistory<ReplyInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), reply.getCard().getList().getBoard(), reply.getCard(),
                EventType.DELETE, EventData.COMMENT, deleteReplyInfo);

        cardHistoryRepository.save(cardHistory);

        // 삭제 수행
        reply.deleteReply();
        boardOffsetService.saveDeleteReply(reply); //Websocket reply 삭제

        return reply;
    }

    @Override
    public List<Reply> getRepliesByCardId(Long cardId) {
        Card card = cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));

        return replyRepository.findAllByCard(card);
    }
}

