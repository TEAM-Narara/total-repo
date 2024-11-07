package com.narara.superboard.attachment.infrastructure;

import com.narara.superboard.attachment.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Boolean existsByCardIdAndIsDeletedFalse(Long cardId);

    Optional<Attachment> findByIdAndIsDeletedFalse(Long attachmentId);
}
