package com.narara.superboard.attachment.service;

import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.attachment.infrastructure.AttachmentRepository;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService{

    private final AttachmentRepository attachmentRepository;
    private final CardRepository cardRepository;

    @Override
    @Transactional
    public Attachment addAttachment(Long cardId, String url) {
        validateUrl(url);
        Card card = getCardById(cardId);
        boolean isCover = isFirstAttachment(cardId);

        Attachment attachment = createAndSaveAttachment(card, url, isCover);

        if (isCover) {
            updateCardCover(card, attachment);
        }

        return attachment;
    }

    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = getAttachmentById(attachmentId);

        if (attachment.getIsCover()) {
            removeCardCover(attachment.getCard());
        }

        markAttachmentAsDeleted(attachment);
        saveAttachment(attachment);
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
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
    }

    private Attachment getAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
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
        card.setCover(Map.of(attachment.getType(), attachment.getUrl()));
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
}
