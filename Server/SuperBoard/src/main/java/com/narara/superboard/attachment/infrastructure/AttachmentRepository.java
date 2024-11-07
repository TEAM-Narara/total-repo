package com.narara.superboard.attachment.infrastructure;

import com.narara.superboard.attachment.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Boolean existsByCardIdAndIsDeletedFalse(Long cardId);
}
