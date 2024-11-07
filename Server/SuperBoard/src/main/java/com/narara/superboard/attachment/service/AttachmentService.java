package com.narara.superboard.attachment.service;

import com.narara.superboard.attachment.entity.Attachment;

public interface AttachmentService {
    // TODO : 첨부파일 등록
    Attachment addAttachment(Long cardId,String url);
    // TODO : 첨부파일 삭제
    void deleteAttachment(Long attachmentId);
    // TODO : 첨부파일 커버 여부 수정
    void updateAttachmentIsCover(Long attachmentId);
}
