package com.narara.superboard.attachment.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.attachment.infrastructure.AttachmentRepository;
import com.narara.superboard.board.service.kafka.BoardOffsetService;
import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.document.CardInfo;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardHistoryRepository;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.fcmtoken.service.AlarmService;
import com.narara.superboard.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final CardRepository cardRepository;
    private final CardHistoryRepository cardHistoryRepository;

    private final BoardOffsetService boardOffsetService;
    private final AlarmService alarmService;

    @Override
    @Transactional
    public Attachment addAttachment(Member member, Long cardId, String url) throws FirebaseMessagingException {
        validateUrl(url);
        Card card = getCardById(cardId);
        boolean isCover = isFirstAttachment(cardId);

        Attachment attachment = createAndSaveAttachment(card, url, isCover);

        if (isCover) {
            updateCardCover(card, attachment);
        }

        boardOffsetService.saveEditCard(card); //Websocket 카드에 첨부파일 추가
        boardOffsetService.saveAddAttachmentDiff(attachment); //Websocket 첨부파일 추가

        // 첨부 파일 추가 로그 기록
        AddAttachmentInfo addAttachmentInfo = new AddAttachmentInfo(cardId, card.getName(), attachment.getId(), url,
                isCover);

        CardHistory<AddAttachmentInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), card.getList().getBoard(),
                card,
                EventType.CREATE, EventData.ATTACHMENT, addAttachmentInfo);

        cardHistoryRepository.save(cardHistory);

        //알림
        alarmService.sendAddCardAttachmentAlarm(member, attachment);

        return attachment;
    }

    @Override
    @Transactional
    public void deleteAttachment(Member member, Long attachmentId) {
        Attachment attachment = getAttachmentById(attachmentId);

        Card card = attachment.getCard();
        if (attachment.getIsCover()) {
            removeCardCover(card);
        }

        markAttachmentAsDeleted(attachment);
        saveAttachment(attachment);

        boardOffsetService.saveEditCard(card); //Websocket 카드 첨부파일 삭제
        boardOffsetService.saveDeleteAttachmentDiff(attachment); //Websocket 첨부파일 삭제

        // 첨부 파일 삭제 로그 기록
        DeleteAttachmentInfo deleteAttachmentInfo = new DeleteAttachmentInfo(
                card.getId(), card.getName(), attachmentId, attachment.getUrl(), attachment.getIsCover());

        CardHistory<DeleteAttachmentInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9)), card.getList().getBoard(),
                card,
                EventType.DELETE, EventData.ATTACHMENT, deleteAttachmentInfo);

        cardHistoryRepository.save(cardHistory);
    }

    @Override
    @Transactional
    public void updateAttachmentIsCover(Long attachmentId) {
        Attachment attachment = getAttachmentById(attachmentId);

        if (attachment.changeIsCover()) {
            updateCardCover(attachment.getCard(), attachment);
        } else {
            removeCardCover(attachment.getCard());
        }

        Attachment savedAttachment = saveAttachment(attachment);

        boardOffsetService.saveEditCard(attachment.getCard()); //Websocket 카드 첨부파일 수정
        boardOffsetService.saveEditAttachmentCoverDiff(savedAttachment); //Websocket 첨부파일 수정
    }

    // Helper Methods

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new NotFoundException("첨부파일", "url");
        }
    }

    private Card getCardById(Long cardId) {
        return cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
    }

    private Attachment getAttachmentById(Long attachmentId) {
        return attachmentRepository.findByIdAndIsDeletedFalse(attachmentId)
                .orElseThrow(() -> new NotFoundEntityException(attachmentId, "첨부파일"));
    }

    private boolean isFirstAttachment(Long cardId) {
        return !attachmentRepository.existsByCardIdAndIsDeletedFalse(cardId);
    }

    private Attachment createAndSaveAttachment(Card card, String url, boolean isCover) {
        Attachment attachment = createAttachment(card, url, isCover);
        saveAttachment(attachment);
        return attachment;
    }

    private Attachment createAttachment(Card card, String url, boolean isCover) {
        return Attachment.builder()
                .card(card)
                .url(url)
                .isCover(isCover)
                .isDeleted(false)
                .build();
    }

    private Attachment saveAttachment(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    private void updateCardCover(Card card, Attachment attachment) {
        card.setCover(new HashMap<>() {{
            put("type", attachment.getType());
            put("value", attachment.getUrl());
        }});
        saveCard(card);
    }

    private void removeCardCover(Card card) {
        card.setCover(new HashMap<>(){{
            put("type", "NONE");
            put("value", "NONE");
        }});
        saveCard(card);
    }

    private void saveCard(Card card) {
        cardRepository.save(card);
    }

    private void markAttachmentAsDeleted(Attachment attachment) {
        attachment.setIsDeleted(true);
        attachment.setIsCover(false);
    }

    public interface AttachmentInfo {
        Long cardId();
        String cardName();
        Long attachmentId();
        String url();
        boolean isCover();
    }

    // 첨부 파일 추가 관련 정보
    public record AddAttachmentInfo(
            Long cardId,
            String cardName,
            Long attachmentId,
            String url,
            boolean isCover
    ) implements AttachmentInfo, CardInfo {
    }

    // 첨부 파일 삭제 관련 정보
    public record DeleteAttachmentInfo(
            Long cardId,
            String cardName,
            Long attachmentId,
            String url,
            boolean isCover
    ) implements AttachmentInfo, CardInfo{
    }
}
