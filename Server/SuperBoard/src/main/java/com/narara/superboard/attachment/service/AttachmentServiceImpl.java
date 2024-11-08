package com.narara.superboard.attachment.service;

import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.attachment.infrastructure.AttachmentRepository;
import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardHistoryRepository;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final CardRepository cardRepository;
    private final CardHistoryRepository cardHistoryRepository;

    @Override
    @Transactional
    public Attachment addAttachment(Member member, Long cardId, String url) {
        validateUrl(url);
        Card card = getCardById(cardId);
        boolean isCover = isFirstAttachment(cardId);

        Attachment attachment = createAndSaveAttachment(card, url, isCover);

        if (isCover) {
            updateCardCover(card, attachment);
        }

        // 첨부 파일 추가 로그 기록
        AddAttachmentInfo addAttachmentInfo = new AddAttachmentInfo(cardId, card.getName(), url, isCover);

        CardHistory<AddAttachmentInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), card.getList().getBoard(), card,
                EventType.ADD, EventData.ATTACHMENT, addAttachmentInfo);

        cardHistoryRepository.save(cardHistory);


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

        // 첨부 파일 삭제 로그 기록
        DeleteAttachmentInfo deleteAttachmentInfo = new DeleteAttachmentInfo(
                card.getId(), card.getName(), attachmentId, attachment.getIsCover());

        CardHistory<DeleteAttachmentInfo> cardHistory = CardHistory.createCardHistory(
                member, LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond(), card.getList().getBoard(), card,
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

        saveAttachment(attachment);
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

    private void saveAttachment(Attachment attachment) {
        attachmentRepository.save(attachment);
    }

    private void updateCardCover(Card card, Attachment attachment) {
        card.setCover(new HashMap<>() {{
            put("type", attachment.getType());
            put("value", attachment.getUrl());
        }});
        saveCard(card);
    }

    private void removeCardCover(Card card) {
        card.setCover(null);
        saveCard(card);
    }

    private void saveCard(Card card) {
        cardRepository.save(card);
    }

    private void markAttachmentAsDeleted(Attachment attachment) {
        attachment.setIsDeleted(true);
        attachment.setIsCover(false);
    }

    // 첨부 파일 추가 관련 정보
    public record AddAttachmentInfo(
            Long cardId,
            String cardName,
            String url,
            boolean isCover
    ) {
    }

    // 첨부 파일 삭제 관련 정보
    public record DeleteAttachmentInfo(
            Long cardId,
            String cardName,
            Long url,
            boolean isCover
    ) {
    }

}
