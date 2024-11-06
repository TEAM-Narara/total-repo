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
        /**
         * <첨부파일 등록시> - 처음인지 아닌지만 체크 해주면 될듯
         * 1. (첨부파일 url, 타입 ) 받아서 추가
         * 2. is_deleted = false인 것만 조회해서 없으면 커버 이미지 여부 true 하고 카드에 커버 추가
         * 3. 조회해서 있으면 그냥 추가하고 커버 이미지 여부 false로 등록
         */
        validateUrl(url);

        Card card = findCardById(cardId);
        boolean isCover = isFirstAttachment(cardId);

        Attachment attachment = createAttachment(card, url, isCover);
        attachmentRepository.save(attachment);

        if (isCover) {
            updateCardCover(card, attachment);
        }

        return attachment;
    }

    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId) {
        /**
         * <첨부파일 삭제 시> - 커버 이미지인지 아닌지만 체크 해주면 될듯
         * 1. (기본키) 받음
         * 2. 커버 이미지 여부가 true면 false로 변경하고 카드 커버 지우고 is_deleted = true로 변경
         * 3. 커버 이미지 여부 false면, is_deleted = true로만 변경
         */
        Attachment attachment = findAttachmentById(attachmentId);

        if (attachment.getIsCover()) {
            removeCardCover(attachment.getCard());
        }

        markAttachmentAsDeleted(attachment);
    }

    @Override
    @Transactional
    public void updateAttachmentIsCover(Long attachmentId) {
        /**
         * <카드에서 커버 변경하는 경우 >
         * 1. (첨부파일 기본키) 받음
         * 2. 토클로 커버 이미지 여부가 false <-> true 변경
         * 3. 바뀐 이미지 여부가 true인 경우 → card cover 설정해주기
         * 4. false인 경우 → card cover null로 변경하기
         */
        Attachment attachment = findAttachmentById(attachmentId);

        if (attachment.changeIsCover()) {
            updateCardCover(attachment.getCard(), attachment);
        } else {
            removeCardCover(attachment.getCard());
        }
    }

    // Helper Methods

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new NotFoundException("첨부파일", "url");
        }
    }

    private Card findCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
    }

    private Attachment findAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NotFoundEntityException(attachmentId, "첨부파일"));
    }

    private boolean isFirstAttachment(Long cardId) {
        return !attachmentRepository.existsByCardIdAndIsDeletedFalse(cardId);
    }

    private Attachment createAttachment(Card card, String url, boolean isCover) {
        return Attachment.builder()
                .card(card)
                .url(url)
                .isCover(isCover)
                .isDeleted(false)
                .build();
    }

    private void updateCardCover(Card card, Attachment attachment) {
        card.setCover(Map.of(attachment.getType(), attachment.getUrl()));
        cardRepository.save(card);
    }

    private void removeCardCover(Card card) {
        card.setCover(null);
        cardRepository.save(card);
    }

    private void markAttachmentAsDeleted(Attachment attachment) {
        attachment.setIsDeleted(true);
        attachment.setIsCover(false);
        attachmentRepository.save(attachment);
    }
}
