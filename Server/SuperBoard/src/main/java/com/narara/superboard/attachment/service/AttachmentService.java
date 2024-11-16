package com.narara.superboard.attachment.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.member.entity.Member;

public interface AttachmentService {
    Attachment addAttachment(Member member, Long cardId, String url) throws FirebaseMessagingException;
    void deleteAttachment(Member member, Long attachmentId);
    void updateAttachmentIsCover(Long attachmentId);
}
