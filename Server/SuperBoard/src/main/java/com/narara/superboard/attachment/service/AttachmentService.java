package com.narara.superboard.attachment.service;

import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.member.entity.Member;

public interface AttachmentService {
    // TODO : 첨부파일 등록
    Attachment addAttachment(Member member, Long cardId, String url);
    // TODO : 첨부파일 삭제
    void deleteAttachment(Member member, Long attachmentId);
    // TODO : 첨부파일 커버 여부 수정
    void updateAttachmentIsCover(Long attachmentId);
}
